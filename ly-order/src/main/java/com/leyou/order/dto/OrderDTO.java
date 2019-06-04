package com.leyou.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-06-04 16:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    /**
     * 收货人地址id
     */
    @NotNull
    private Long addressId;
    /**
     * 付款类型
     */
    @NotNull
    private Integer paymentType;
    /**
     * 订单详情
     */
    @NotNull
    private List<CartDTO> carts;
}
