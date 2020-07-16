package com.lhq.superboot.service.msg;

/**
 * @Description: 消息封装工具类的基类, 这里采用泛型, 方便后期功能扩展
 * @author: lihaoqi
 * @date: 2019年9月6日 下午12:31:59
 * @version: v1.0.0
 */
public interface BaseMessageService<T> {

    /**
     * @param message
     * @return
     * @Description: 将回复的信息对象转xml格式给微信
     */
    public abstract String messageToxml(T t);

    /**
     * @param FromUserName
     * @param ToUserName
     * @param Content
     * @return
     * @Description: 回复的信息封装
     */
    public abstract String initMessage(String FromUserName, String ToUserName, String Content);

}
