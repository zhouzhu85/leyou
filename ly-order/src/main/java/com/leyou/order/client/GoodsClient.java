package com.leyou.order.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-06-04 18:00
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {

}
