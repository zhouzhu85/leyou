package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhouzhu
 */
@Service
@Slf4j
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository repository;

    @Autowired
    private ElasticsearchTemplate template;

    public Goods buildGoods(Spu spu){
        Long spuId = spu.getId();
        //查询分类
        List<Category> categories = categoryClient.queryCategoryByids(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
        }
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
        if (CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOND);
        }
        //对sku进行处理
        List<Map<String,Object>> skus=new ArrayList<>();
        //价格集合
        Set<Long> priceSet=new HashSet<>();

        skuList.forEach(sku->{
            Map<String,Object> map=new HashMap<>(16);
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("images", StringUtils.substringBefore(sku.getImages(),","));
            skus.add(map);
            //处理价格
            priceSet.add(sku.getPrice());
        });
        //搜索字段
        String all = spu.getTitle()+ StringUtils.join(names," ")+brand.getName();
        //查询规格参数
        List<SpecParam> params = specificationClient.queryParamByGid(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(params)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOND);
        }
        //查询商品详情
        SpuDetail spuDetail = goodsClient.queryDetailBySpuId(spuId);
        //获取通用的规格参数
        Map<Long, String> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, String.class);
        //获取特有规格参数
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {});
        //规格参数，key是规格参数的名字，值是规格参数的值
        Map<String,Object> specs=new HashMap<>(16);
        params.forEach(param->{
            //规格名称
            String key = param.getName();
            Object value="";
            //判断是否是通用规格
            if (param.getGeneric()){
                value=genericSpec.get(param.getId());
                //判断是否是数值类型
                if (param.getNumeric()){
                    value=chooseSegment(value.toString(),param);
                }

            }else {
                value=specialSpec.get(param.getId());
            }
            specs.put(key,value);
        });
        //构建goods对象
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        //搜索字段，包含标题，分类，品牌，规格等
        goods.setAll(all);
        // 所有sku的价格集合
        goods.setPrice(priceSet);
        //  所有sku的集合json格式
        goods.setSkus(JsonUtils.toString(skus));
        //  所有的可搜索的规格参数
        goods.setSpecs(specs);
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest request) {
        int page=request.getPage()-1;
        int size=request.getSize();
        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        //分页
        queryBuilder.withPageable(PageRequest.of(page,size));
        //排序
        String sortBy = request.getSortBy();
        Boolean desc = request.getDescending();
        if (!StringUtils.isEmpty(sortBy)){
            //如果不为空，则进行排序
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc? SortOrder.DESC:SortOrder.ASC));
        }
        //过滤
            //基本查询条件
        QueryBuilder basicQuery = QueryBuilders.matchQuery("all", request.getKey());
        queryBuilder.withQuery(basicQuery);
        //聚合分类和品牌
        String categoryAggName="category_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        String brandAggName="brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //查询
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(),Goods.class);
        //解析结果
        long total = result.getTotalElements();
        int totalPage=result.getTotalPages();
        List<Goods> goodsList = result.getContent();
        //解析聚合结果
        Aggregations aggs = result.getAggregations();
        //分类
        List<Category> categories = parseCategoryAgg(aggs.get(categoryAggName));
        //品牌
        List<Brand> brands = parseBrandAgg(aggs.get(brandAggName));
        //规格参数集合
        List<Map<String,Object>> specs=null;
        if (categories!=null && categories.size()==1){
            specs=buildSpecifictationAgg(categories.get(0).getId(),basicQuery);
        }
        return new SearchResult(total,totalPage,goodsList,categories,brands,specs);
    }

    /**
     *  构建规格聚合
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> buildSpecifictationAgg(Long cid, QueryBuilder basicQuery) {
        List<Map<String,Object>> specs=new ArrayList<>();
        //查询需要聚合的规格
        List<SpecParam> params = specificationClient.queryParamByGid(null, cid, true);
        //聚合
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();
        //加上基本查询条件
        queryBuilder.withQuery(basicQuery);
        params.forEach(param->{
            String name = param.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs"+name+".keyword"));
        });
        //获取结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //解析结果
        Aggregations aggs = result.getAggregations();
        params.forEach(param->{
            String name = param.getName();
            LongTerms terms = aggs.get(name);
        });
        return specs;
    }

    /**
     * 解析品牌聚合结果
     * @param terms
     * @return
     */
    private List<Brand> parseBrandAgg(LongTerms terms){
        try {
            //得到品牌id集合
            List<Long> bids = terms.getBuckets().stream()
                    .map(bucket -> bucket.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            //根据id查询品牌
            return brandClient.queryBrandByIds(bids);
        } catch (Exception e) {
            log.error("品牌聚合出现异常: ",e);
            return null;
        }
    }

    /**
     * 解析商品分类集合结果
     * @param terms
     * @return
     */
    private List<Category> parseCategoryAgg(LongTerms terms){
        try {
            //分类id集合
            List<Long> cids = terms.getBuckets().stream()
                    .map(bucket -> bucket.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            //根据id查询分类名称
            return categoryClient.queryCategoryByids(cids);
        } catch (Exception e) {
            log.error("商品分类查询异常：",e);
            return null;
        }
    }
    /**
     * 构建基本查询条件
     * @param queryBuilder
     * @param request
     */
    private void searchWithPageAndSort(NativeSearchQueryBuilder queryBuilder,SearchRequest request){
        //分页参数
        int page=request.getPage();
        int size=request.getSize();
        //分页
        queryBuilder.withPageable(PageRequest.of(page-1,size));
        //排序
        String sortBy = request.getSortBy();
        Boolean desc = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)){
            //如果不为空，则进行排序
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc?SortOrder.DESC:SortOrder.ASC));
        }
    }
}
