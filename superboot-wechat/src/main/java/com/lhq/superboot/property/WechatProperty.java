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
public class WechatProperty {

    @Value("${wechat.miniProgram.appid}")
    private String appid;

    @Value("${wechat.miniProgram.mchid}")
    private String mchid;

    @Value("${wechat.miniProgram.secret}")
    private String secret;

    @Value("${wechat.miniProgram.apiKey}")
    private String apiKey;

    @Value("${wechat.miniProgram.grantType}")
    private String grantType;

    @Value("${wechat.miniProgram.signType}")
    private String signType;

    @Value("${wechat.miniProgram.authUrl}")
    private String authUrl;

}
