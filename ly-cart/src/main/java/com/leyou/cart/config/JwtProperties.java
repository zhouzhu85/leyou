package com.leyou.cart.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-05-30 10:20
 */
@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {
    private String pubKeyPath;
    private String cookieName;
    /**
     * 公钥
     */
    private PublicKey publicKey;
    /**
     *  对象一旦实例化后，就应该读取公钥和私钥
     * @throws Exception
     */
    @PostConstruct
    public void init() throws Exception {
        //读取公钥
        this.publicKey= RsaUtils.getPublicKey(pubKeyPath);
    }
}

