package com.lhq.superboot.service;

/**
 * @Description: 公众号事件处理
 * @author: lihaoqi
 * @date: 2019年9月10日 下午3:39:24
 * @version: v1.0.0
 */
public interface OfficialEventHandleService {

    /**
     * @param EventKey
     * @return
     * @Description: 通过eventKey处理消息并返回msg
     */
    public String HandleEvent(String EventKey, String paramJson);
}
