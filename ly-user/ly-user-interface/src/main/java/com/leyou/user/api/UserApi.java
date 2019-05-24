package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-05-24 15:14
 */
public interface UserApi {
    /**
     * 根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    User queryUserByUsernameAndPassword(@RequestParam("username") String username, @RequestParam("password") String password);
}
