package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.Spu;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    public PageResult<Spu> queryGoodsByPage(Integer page, Integer rows, Boolean saleable, String key) {
        PageHelper.startPage(page,rows);
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //搜索
        if (!StringUtils.isNotBlank(key)){
            criteria.orLike("title","%"+key+"%");
        }
        //上下架
        if (saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
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
}
