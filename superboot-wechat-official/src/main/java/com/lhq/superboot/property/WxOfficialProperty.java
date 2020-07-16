package com.lhq.superboot.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @Description: 公众号配置文件
 * @author: lihaoqi
 * @date: 2019年9月5日 下午5:33:32
 * @version: v1.0.0
 */
@Data
@Component
public class WxOfficialProperty {

    @Value("${wechat.official.token}")
    private String token;

    @Value("${wechat.official.grantType}")
    private String grantType;

    @Value("${wechat.official.accessTokenKey}")
    private String accessTokenKey;

    @Value("${wechat.official.accessTokenUrl}")
    private String accessTokenUrl;

    @Value("${wechat.official.createMenuUrl}")
    private String createMenuUrl;

    @Value("${wechat.official.deleteMenuUrl}")
    private String deleteMenuUrl;

    @Value("${wechat.official.queryMenuUrl}")
    private String queryMenuUrl;

    @Value("${wechat.official.queryPersonMsgUrl}")
    private String queryPersonMsgUrl;
}
