package com.leyou.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-06-04 17:26
 */
@Data
@ConfigurationProperties(prefix = "ly.worker")
public class IdworkerProperties {
    /**
     * 当前机器id
     */
    private long workerId;
    /**
     * 序列号
     */
    private long dataCenterId;
}
