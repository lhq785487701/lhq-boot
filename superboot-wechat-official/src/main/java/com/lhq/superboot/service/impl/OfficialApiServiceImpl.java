package com.lhq.superboot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.property.WechatProperty;
import com.lhq.superboot.property.WxOfficialProperty;
import com.lhq.superboot.service.OfficialApiService;
import com.lhq.superboot.util.HttpClientUtils;
import com.lhq.superboot.util.RedisUtils;

/**
 * @Description: 公众号接口实现类
 * @author: lihaoqi
 * @date: 2019年9月6日 下午3:19:15
 * @version: v1.0.0
 */
@Service
public class OfficialApiServiceImpl implements OfficialApiService {

    private static final Logger logger = LoggerFactory.getLogger(OfficialApiService.class);

    @Autowired
    private WxOfficialProperty wxOffProperty;

    @Autowired
    private WechatProperty wechatProperty;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String getAccessToken() {
        Object accessTokenObj = redisUtils.get(wxOffProperty.getAccessTokenKey());
        String accessToken;

        if (accessTokenObj != null) {
            logger.debug("[getAccessToken] -> 获取access_token{}", accessTokenObj);
            accessToken = accessTokenObj.toString();
        } else {
            logger.debug("[getAccessToken] -> access_token不存在或者已经过期，重新获取");
            StringBuilder url = new StringBuilder().append(wxOffProperty.getAccessTokenUrl()).append("?grant_type=")
                    .append(wxOffProperty.getGrantType()).append("&appid=").append(wechatProperty.getAppid())
                    .append("&secret=").append(wechatProperty.getSecret());
            logger.debug("[getAccessToken] -> 获取access_token的url:{}", url);
            JSONObject resultJson = HttpClientUtils.httpGet(url.toString());
            if (resultJson == null) {
                logger.error("[getAccessToken] -> 获取access_token失败，无法获取返回值");
                throw new SuperBootException("lhq-superboot-official-0001");
            }
            // 获取失败
            if (resultJson.containsKey("errcode")) {
                String errmsg = resultJson.getString("errmsg");
                logger.error("[getAccessToken] -> 获取access_token失败，失败原因{}", errmsg);
                throw new SuperBootException("lhq-superboot-official-0002", errmsg);
            }
            accessToken = resultJson.getString("access_token");
            int expireIn = resultJson.getIntValue("expires_in");
            logger.debug("[getAccessToken] -> 获取access_token成功，access_token={}, expireIn={}", accessToken, expireIn);
            redisUtils.set(wxOffProperty.getAccessTokenKey(), accessToken, expireIn);
        }
        return accessToken;
    }

    @Override
    public String getPersonalMsg(String openid) {
        String personMsg = null;
        String accessToken = getAccessToken();
        String url = wxOffProperty.getQueryPersonMsgUrl().replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
        logger.debug("[getPersonalMsg] -> 获取个人信息url:{}", url);

        JSONObject jsonObject = HttpClientUtils.httpGet(url);
        logger.debug("[getPersonalMsg] -> 获取个人信息: {}", jsonObject);
        if (null != jsonObject) {
            int errcode = jsonObject.getIntValue("errcode");
            if (errcode != 0) {
                logger.error("[getPersonalMsg] -> 获取个人信息错误，错误信息: {}", jsonObject.getString("errmsg"));
                return null;
            }
            int subscribe = jsonObject.getIntValue("subscribe");
            if (subscribe == 1) {
                personMsg = jsonObject.toString();
            } else {
                logger.warn("[getPersonalMsg] -> 该用户尚未关注公众号，无法获取个人信息");
            }
        }
        return personMsg;
    }
}
