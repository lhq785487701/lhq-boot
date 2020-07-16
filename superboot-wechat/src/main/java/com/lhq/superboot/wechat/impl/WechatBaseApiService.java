package com.lhq.superboot.wechat.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lhq.superboot.domain.WechatAuthResult;
import com.lhq.superboot.property.WechatProperty;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.HttpClientUtils;
import com.lhq.superboot.util.StringUtils;
import com.lhq.superboot.wechat.WechatBaseApi;

/**
 * @Description: wechat基础接口实现
 * @author: lihaoqi
 * @date: 2019年4月24日
 * @version: 1.0.0
 */
@Service
public class WechatBaseApiService implements WechatBaseApi {

    private static final Logger logger = LoggerFactory.getLogger(WechatBaseApiService.class);

    @Autowired
    private WechatProperty wechatProperty;

    @Override
    public WechatAuthResult getUserOpenid(String code) {
        logger.debug("入参：code:{}", code);
        if (StringUtils.isNotEmpty(code)) {
            String url = wechatProperty.getAuthUrl() + "?appid=" + wechatProperty.getAppid() + "&secret=" +
                    wechatProperty.getSecret() + "&js_code=" + code + "&grant_type=" + wechatProperty.getGrantType();
            JSONObject resultJson = HttpClientUtils.httpGet(url);

            logger.debug("查询结果：json:{}", resultJson);
            String result = FastJsonUtils.toJSONFeatures(resultJson);
            return (WechatAuthResult) FastJsonUtils.convertJsonToObject(result,
                    WechatAuthResult.class);
        } else {
            logger.error("入参不能为空");
            return null;
        }
    }

}
