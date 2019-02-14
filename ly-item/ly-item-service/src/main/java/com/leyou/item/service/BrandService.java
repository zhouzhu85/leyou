package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        PageHelper.startPage(page,rows);
        Example example=new Example(Brand.class);
        if (StringUtil.isNotEmpty(key)){
            example.createCriteria().orLike("name","%"+key+"%")
                    .orEqualTo("letter",key.toUpperCase());
        }
        if (StringUtil.isNotEmpty(sortBy)){
            String orderByClause=sortBy+(desc?" DESC":" ASC");
            example.setOrderByClause(orderByClause);
        }
        List<Brand> list = brandMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        PageInfo info=new PageInfo(list);
        return new PageResult<>(info.getTotal(),list);
    }

    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌
        brand.setId(null);
        int count = brandMapper.insert(brand);
        if (count!=1){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //新增中间表
        for (Long cid:cids){
            count = brandMapper.insertCategoryBrand(cid, brand.getId());
            if (count !=1){
                throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
            }
        }
    }
}
