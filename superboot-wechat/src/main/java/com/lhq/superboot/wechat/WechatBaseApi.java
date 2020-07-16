package com.lhq.superboot.wechat;

import com.lhq.superboot.domain.WechatAuthResult;

/**
 * @Description: wechat基础接口实现
 * @author: lihaoqi
 * @date: 2019年4月24日
 * @version: 1.0.0
 */
public interface WechatBaseApi {

    /**
     * @param code
     * @return
     * @Description: 获取用户openid
     */
    public WechatAuthResult getUserOpenid(String code);
}
