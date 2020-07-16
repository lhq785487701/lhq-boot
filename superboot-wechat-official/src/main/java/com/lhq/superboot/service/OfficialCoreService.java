package com.lhq.superboot.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 微信公众号核心接口
 * @author: lihaoqi
 * @date: 2019年9月10日 上午11:18:46
 * @version: v1.0.0
 */
public interface OfficialCoreService {

    /**
     * @param req
     * @return
     * @Description: 处理微信服务器传过来的消息并封装返回值
     */
    public String officialMessageHandle(HttpServletRequest req);
}
