package com.leyou.auth;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-05-22 17:19
 */
public class JwtTest {
    private static final String publicKeyPath="E:\\leyou_atuh_key\\rsa.pub";
    private static final String privateKeyPath="E:\\leyou_atuh_key\\rsa.pri";
    private PrivateKey privateKey;
    private PublicKey publicKey;
    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(publicKeyPath,privateKeyPath,"234");
    }
    @Before
    public void testGetRsa() throws Exception{
        privateKey=RsaUtils.getPrivateKey(privateKeyPath);
        publicKey=RsaUtils.getPublicKey(publicKeyPath);
    }
    @Test
    public void generateToken() {
        //生成Token
        String s = JwtUtils.generateToken(new UserInfo(20L, "Jack"), privateKey, 5);
        System.out.println("s = " + s);
    }
    @Test
    public void parseToken() {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiSmFjayIsImV4cCI6MTU1ODUxNzkxOX0.JxkDeT88xn8UB-PucSNH88RM3dJhN4JLJe2OWGL0JyrP26J8VOi03UhLVLoOG-bUu6ZPnILhObxETrqPRhV9Lw9eJ3DVmwZq9327asC8znrNpxbE3XNu71tkeHEbDX7J4NuLFLVpcyXJq6D9hUuiP95zXKQejAInZTV0Jw0iuo0";
        UserInfo userInfo = JwtUtils.getUserInfo(publicKey, token);
        System.out.println("id:" + userInfo.getId());
        System.out.println("name:" + userInfo.getUsername());
    }
}
