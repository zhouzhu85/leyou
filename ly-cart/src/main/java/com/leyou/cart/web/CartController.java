package com.leyou.cart.web;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import com.netflix.ribbon.proxy.annotation.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-05-30 14:41
 */
@RestController
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * 新增购物车
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * 合并localStorage数据到购物车
     * @param carts
     * @return
     */
    @PostMapping("more")
    public ResponseEntity<Void> mergeLocalStorageToCart(@RequestBody List<Cart> carts){
        cartService.mergeLocalStorageToCart(carts);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询登录用户的购物车
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Cart>> queryCarList(){
        return ResponseEntity.ok(cartService.queryCartList());
    }

    /**
     * 修改购物车的商品数量
     * @param skuId
     * @param num
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateCartNum(@RequestParam("id") Long skuId,@RequestParam Integer num){
        cartService.updateNum(skuId,num);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId){
        cartService.deleteCartById(skuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
