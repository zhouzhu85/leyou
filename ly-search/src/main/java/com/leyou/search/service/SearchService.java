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
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        //过滤
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()));
        //查询
        Page<Goods> result = repository.search(queryBuilder.build());
        //解析结果
        long total = result.getTotalElements();
        List<Goods> goodsList = result.getContent();
        return new PageResult<>(total,goodsList);
    }
}
