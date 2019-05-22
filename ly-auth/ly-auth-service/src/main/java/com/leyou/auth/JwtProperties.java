package com.leyou.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-05-22 17:51
 */
@Data
@ConfigurationProperties("ly.jwt")
public class JwtProperties {
        private String secret;
        private String pubKeyPath;
        private String priKeyPath;
        private long expire;
}
