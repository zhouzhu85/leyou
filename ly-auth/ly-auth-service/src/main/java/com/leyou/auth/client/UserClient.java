package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-05-24 15:19
 */
@FeignClient("user-service")
public interface UserClient extends UserApi {
}
