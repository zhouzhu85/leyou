package com.leyou.auth.web;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-05-24 14:49
 */
@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    @Value("${ly.jwt.cookieName}")
    private String cookieName;

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestParam("username") String username,
                                        @RequestParam("password") String password,
                                        HttpServletResponse response,
                                        HttpServletRequest request){
        String token=authService.login(username,password);
        CookieUtils.newBuilder(response).httpOnly().request(request).build(cookieName,token);
        return ResponseEntity.ok(token);
    }
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN") String token,
                                           HttpServletRequest request,
                                           HttpServletResponse response){
        try {
            //解析token
            UserInfo info = JwtUtils.getUserInfo(jwtProperties.getPublicKey(), token);
            //刷新token，重新生成token
            String newToken = JwtUtils.generateToken(info, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            //写入cookie
            CookieUtils.newBuilder(response).httpOnly().request(request).build(cookieName,newToken);
            //已登录，返回用户信息
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            //token已过期，或者token被篡改
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
    }
}
