package com.lhq.superboot.service;

/**
 * @Description: 手机相关接口
 * @author: lihaoqi
 * @date: 2019年4月24日
 * @version: 1.0.0
 */
public interface PhoneService {

    /**
     * @param phone
     * @return
     * @Description: 忘记密码功能，设置一个随机数进redis，与phone形成token返回前端，用于修改密码接口的安全性校验
     */
    public String setPhoneRandomInCache(String phone);

    /**
     * @param token
     * @return
     * @Description: 忘记密码功能，将token解密，读出随机数，对比校验
     */
    public boolean validatePhoneRandom(String token);


}
