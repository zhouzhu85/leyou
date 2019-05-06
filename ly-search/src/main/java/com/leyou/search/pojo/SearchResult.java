package com.leyou.search.pojo;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author zhouzhu
 */
@Data
public class SearchResult extends PageResult<Goods> {
    public SearchResult(){
    }
    /**
     * 分类集合 待选项
     */
    private List<Category> categories;
    /**
     * 品牌集合 待选项
     */
    private List<Brand> brands;
    /**
     * 规格集合 key及待选项
     */
    private List<Map<String,Object>> specs;

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }
}
