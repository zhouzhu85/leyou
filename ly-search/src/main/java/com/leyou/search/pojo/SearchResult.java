package com.leyou.search.pojo;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;

import java.util.List;

/**
 * @author zhouzhu
 */
public class SearchResult extends PageResult<Goods> {
    /**
     * 分类集合
     */
    private List<Category> categories;
    /**
     * 品牌集合
     */
    private List<Brand> brands;
    public SearchResult(Long total,Integer totalPage,List<Goods> items,List<Category> categories,List<Brand> brands){
        super(total,totalPage,items);
        this.categories=categories;
        this.brands=brands;
    }
}
