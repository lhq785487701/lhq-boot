package com.lhq.superboot.service;

import com.lhq.superboot.vo.PcLoginVo;

/**
 * @Description: login 接口
 * @author: lihaoqi
 * @date: 2019年4月19日
 * @version: 1.0.0
 */
public interface LoginService {

    /**
     * @param pcLoginQo
     * @return
     * @Description: pc或者后台通过用户名登录
     */
    public String userAccountLogin(PcLoginVo pcLoginVo);

    /**
     * @param phone
     * @param channelFlg
     * @return
     * @Description: pc或者后台通过手机验证码免密登录
     */
    public String userPhoneLogin(String phone, String channelFlg);

    /**
     * @param phone
     * @return
     * @Description: 微信小程序登录
     */
    public String wxLogin(String phone);

}
