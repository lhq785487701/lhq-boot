package com.lhq.superboot.service;

/**
 * @Description: APP相关接口
 * @author: lihaoqi
 * @date: 2019年4月30日
 * @version: 1.0.0
 */
public interface AppService {

    /**
     * @Description: 生成一个sessionKey，存入redis，设置过期时间
     */
    public String setSessionKey(String phone);

    /**
     * @Description: 判断sessionKey是否还存着，不存在则表示用户sessionKey已过期，需要重新手机授权登录
     */
    public boolean isExpireSessionKey(String phone, String sessionKey);

}
