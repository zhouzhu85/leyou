package com.leyou.user.web;

import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-05-21 16:49
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

}
