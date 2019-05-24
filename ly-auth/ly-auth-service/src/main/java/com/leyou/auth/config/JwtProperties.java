package com.leyou.auth.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-05-22 17:51
 */
@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {
    private String secret;
    private String pubKeyPath;
    private String priKeyPath;
    private int expire;
    private String cookieName;
    /**
     * 公钥
     */
    private PublicKey publicKey;
    /**
     * 私钥
     */
    private PrivateKey privateKey;

    /**
     *  对象一旦实例化后，就应该读取公钥和私钥
     * @throws Exception
     */
    @PostConstruct
    public void init() throws Exception {
        //公钥私钥如果不存在，就先生成
        File pubPath=new File(pubKeyPath);
        File priPath=new File(priKeyPath);
        if (!pubPath.exists() || !priPath.exists()){
            //生成公钥私钥
            RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
        }
        //读取公钥和私钥
        this.publicKey= RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey=RsaUtils.getPrivateKey(priKeyPath);
    }
}
