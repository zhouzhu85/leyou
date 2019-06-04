package com.leyou.order.dto;

import lombok.Data;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-06-04 17:38
 */
@Data
public class AddressDTO {
    private Long id;
    private String name;// 收件人姓名
    private String phone;// 电话
    private String state;// 省份
    private String city;// 城市
    private String district;// 区
    private String address;// 街道地址
    private String  zipCode;// 邮编
    private Boolean isDefault;
}
