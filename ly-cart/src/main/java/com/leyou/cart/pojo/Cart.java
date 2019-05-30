package com.leyou.cart.pojo;

import lombok.Data;

/**
 * @author zhouzhu
 * @Description 购物车实体类
 * @create 2019-05-30 14:37
 */
@Data
public class Cart {
    private Long skuId;
    private String title;
    private String image;
    private String price;
    private Integer num;
    /**
     * 商品规格参数
     */
    private String ownSpec;
}
