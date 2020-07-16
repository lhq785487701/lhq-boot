package com.lhq.superboot.service;

/**
 * @Description: 公众号接口(暂全部 ， 往后接口多了分service)
 * @author: lihaoqi
 * @date: 2019年9月6日 下午3:16:10
 * @version: v1.0.0
 */
public interface OfficialApiService {

    /**
     * @return
     * @Description: 获取access_token
     */
    public String getAccessToken();

    /**
     * @param openid
     * @return
     * @Description: 通过openid获取个人基本信息
     */
    public String getPersonalMsg(String openid);

}
