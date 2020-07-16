package com.lhq.superboot.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @Description: 将wechat配置文件读取
 * @author: lihaoqi
 * @date: 2019年4月18日
 * @version: 1.0.0
 */
@Data
@Component
public class WxPayProperty {

    @Value("${wechat.miniProgram.spbillCreateIp}")
    private String spbillCreateIp;

    @Value("${wechat.miniProgram.tradeType}")
    private String tradeType;

    @Value("${wechat.miniProgram.feeType}")
    private String feeType;

    @Value("${wechat.miniProgram.unifiedOrderUrl}")
    private String unifiedOrderUrl;

    @Value("${wechat.miniProgram.queryOrderUrl}")
    private String queryOrderUrl;

    @Value("${wechat.miniProgram.closeOrderUrl}")
    private String closeOrderUrl;

    @Value("${wechat.miniProgram.refundUrl}")
    private String refundUrl;

    @Value("${wechat.miniProgram.refundQueryUrl}")
    private String refundQueryUrl;

    @Value("${wechat.miniProgram.wechatOrderKey}")
    private String wechatOrderKey;

    @Value("${wechat.miniProgram.wechatOrderExpire}")
    private Long wechatOrderExpire;


}
