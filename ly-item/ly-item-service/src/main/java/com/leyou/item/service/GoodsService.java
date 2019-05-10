package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.DetailMapper;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private DetailMapper detailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;


    public PageResult<Spu> queryGoodsByPage(Integer page, Integer rows, Boolean saleable, String key) {
        PageHelper.startPage(page,rows);
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //搜索
        if (StringUtils.isNotBlank(key)){
            criteria.orLike("title","%"+key+"%");
        }
        //上下架
        if (saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        criteria.andEqualTo("valid",true);
        example.setOrderByClause("last_update_time DESC");
        List<Spu> spuList = spuMapper.selectByExample(example);
        //解析分类和品牌的名称
        loadCategoryAndBrandName(spuList);

        PageInfo info=new PageInfo(spuList);
        return new PageResult<>(info.getTotal(),spuList);
    }

    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu:spus){
            List<String> names = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names,"/"));
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void saveGoods(Spu spu) {
        //新增spu
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(true);
        int count = spuMapper.insert(spu);
        if (count!=1){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        //新增商品详情
        SpuDetail detail = spu.getSpuDetail();
        detail.setSpuId(spu.getId());
        detailMapper.insert(detail);

        //新增sku和stock
        saveSkuAndStock(spu,count);

    }

    public SpuDetail queryDetailBySpuId(Long spuId) {
        SpuDetail spuDetail = detailMapper.selectByPrimaryKey(spuId);
        if (spuDetail==null){
            throw new LyException(ExceptionEnum.GOODS_DETAIL_NOT_FOUND);
        }
        return spuDetail;
    }

    public List<Sku> querySkusBySpuId(Long id) {
        Sku sku=new Sku();
        sku.setSpuId(id);
        List<Sku> skuList = skuMapper.select(sku);
        //java8之前写法
//        for (Sku sku:skuList){
        //多次查询影响性能
//            sku.setStock(stockMapper.selectByPrimaryKey(sku.getId()).getStock());
//        }

        //java8写法
        //查询库存
        List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        //批量一次查完库存
        List<Stock> stockList = stockMapper.selectByIdList(ids);
        //把stock变成map，key是sku的id，value是库存数量
        Map<Long, Integer> stockMap = stockList.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        skuList.forEach(s->s.setStock(stockMap.get(s.getId())));
        return skuList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(Spu spu) {
        if (spu.getId()==null){
            throw new LyException(ExceptionEnum.GOODS_Id_IS_EMPTY);
        }
        Sku sku=new Sku();
        sku.setSpuId(spu.getId());
        List<Sku> skuList = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skuList)){
            //删除sku和stock
            skuMapper.delete(sku);
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(ids);
        }
        //修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count!=1){
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //修改detail
         count = detailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if (count!=1){
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //新增sku和stock
        saveSkuAndStock(spu,count);
    }
    private void saveSkuAndStock(Spu spu,int count){
        List<Stock> stockList=new ArrayList<>();
        //新增sku
        List<Sku> skus = spu.getSkus();
        for (Sku sku:skus){
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());
            count = skuMapper.insert(sku);
            if (count!=1){
                throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
            //新增库存
            Stock stock=new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockList.add(stock);
        }
        //批量新增库存
        stockMapper.insertList(stockList);
    }
    @Transactional(rollbackFor =Exception.class)
    public void deleteGoods(Long id) {
        Spu spu=new Spu();
        spu.setId(id);
        spu.setValid(false);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count!=1){
            throw new LyException(ExceptionEnum.GOODS_DETAIL_NOT_FOUND);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateSaleable(Long spuId, Boolean saleable) {
        Spu spu=new Spu();
        spu.setId(spuId);
        spu.setSaleable(saleable);
        int count = spuMapper.updateByPrimaryKeySelective(spu);
        if (count!=1){
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
    }

    public Spu querySpuById(Long id) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if (spu==null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOND);
        }
        //查询sku
        spu.setSkus(querySkusBySpuId(id));
        //查询商品详情
        spu.setSpuDetail(queryDetailBySpuId(id));

        return spu;
    }
}
