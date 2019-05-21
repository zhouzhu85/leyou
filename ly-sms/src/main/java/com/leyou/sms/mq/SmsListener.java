package com.leyou.sms.mq;

import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author zhouzhu
 * @Description
 * @create 2019-05-21 14:02
 */
@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {
    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private SmsProperties smsProperties;

    /**
     * 发送短信验证码
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "sms.verify.code.queue",durable = "true"),
            exchange = @Exchange(name = "ly.sms.exchange",type = ExchangeTypes.TOPIC),
            key = {"sms.verify.code"}
    ))
    public void sms(Map<String,String> msg){
        if (CollectionUtils.isEmpty(msg)){
            return;
        }
        String phone = msg.remove("phone");
        if (StringUtils.isEmpty(phone)){
            return;
        }
        smsUtils.sendSms(phone,smsProperties.getSignName(),smsProperties.getVerifyCodeTemplate(), JsonUtils.toString(msg));
        log.info("[短信服务]，发送短信验证码，手机号:{}",phone);
    }
}
