package com.lhq.superboot.service;

import com.lhq.superboot.domain.UserManager;

/**
 * @Description 对外接口api的key与secret
 * @author: lihaoqi
 * @date: 2019年7月24日 上午10:24:33
 * @version: v1.0.0
 */
public interface UserApiService {

    /**
     * @return
     * @Description: 获取当前登录人对外接口key
     */
    public String getApiKey();

    /**
     * @param userId
     * @return
     * @Description: 通过用户id获取对外接口key
     */
    public String getApiKey(String userId);

    /**
     * @param appKey
     * @return
     * @Description: 获取对外接口秘钥
     */
    public String getApiSecret(String appKey);

    /**
     * @param appKey
     * @return
     * @Description: 通过apikey获取用户信息
     */
    public UserManager getUserByApikey(String appKey);
}
