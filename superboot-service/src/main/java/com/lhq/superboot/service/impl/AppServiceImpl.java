package com.lhq.superboot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.lhq.superboot.service.AppService;
import com.lhq.superboot.util.RedisUtils;
import com.lhq.superboot.util.UuidUtils;

/**
 * @author: lihaoqi
 * @date: 2019年4月30日
 * @version: 1.0.0
 */
@Service
public class AppServiceImpl implements AppService {

    /**
     * 存放app小程序sessionKey的redis空间名
     **/
    private static final String APP_KEY_PREFIX = "lhq:superboot:app:sessionkey:";

    /**
     * session存放时间30*24*60*60(30天)
     **/
    private static final int APP_KEY_EXPIRE = 2592000;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String setSessionKey(String phone) {
        String sessionKey = UuidUtils.getUuid();

        redisUtils.set(APP_KEY_PREFIX + phone, sessionKey, APP_KEY_EXPIRE);

        return sessionKey;
    }

    @Override
    public boolean isExpireSessionKey(String phone, String sessionKey) {
        String sessionStr = (String) redisUtils.get(APP_KEY_PREFIX + phone);
        return !StringUtils.isEmpty(sessionStr) && sessionStr.equals(sessionKey);
    }

}
