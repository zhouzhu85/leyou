package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-05-21 11:42
 */
@ConfigurationProperties(prefix = "ly.sms")
@Data
public class SmsProperties {
    String accessKeyId;
    String accessKeySecret;
    String signName;
    String verifyCodeTemplate;
}
