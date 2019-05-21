package com.leyou.user.service;

import com.leyou.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-05-21 16:48
 */
public class UserService {
    @Autowired
    private UserMapper userMapper;
}
