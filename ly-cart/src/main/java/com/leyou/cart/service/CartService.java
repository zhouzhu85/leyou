package com.leyou.cart.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-05-30 14:43
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX="cart:userId:";

    public void addCart(Cart cart) {
        //获取用户信息
        UserInfo user = UserInterceptor.getUser();
        String key=KEY_PREFIX+user.getId();
        String hashKey=cart.getSkuId().toString();
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        //记录num
        Integer num = cart.getNum();
        //判断当前购物车商品是否存在
        if (operations.hasKey(hashKey)) {
            //是修改数量
            String json = operations.get(hashKey).toString();
            cart = JsonUtils.toBean(json, Cart.class);
            cart.setNum(cart.getNum()+ num);
        }
        //写回redis
        operations.put(hashKey,JsonUtils.toString(cart));
    }
}
