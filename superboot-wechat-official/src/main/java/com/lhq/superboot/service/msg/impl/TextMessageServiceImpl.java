package com.lhq.superboot.service.msg.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.lhq.superboot.domain.MessageText;
import com.lhq.superboot.enums.OfficialMsgType;
import com.lhq.superboot.service.msg.BaseMessageService;
import com.thoughtworks.xstream.XStream;

/**
 * @Description: 重载发送消息的封装
 * @author: lihaoqi
 * @date: 2019年9月6日 下午12:34:45
 * @version: v1.0.0
 */
@Service("textMessage")
public class TextMessageServiceImpl implements BaseMessageService<MessageText> {

    private static final Logger logger = LoggerFactory.getLogger(BaseMessageService.class);

    /**
     * @Description: 将发送消息封装成对应的xml格式
     */
    public String messageToxml(MessageText message) {
        XStream xstream = new XStream();
        xstream.alias("xml", message.getClass());
        return xstream.toXML(message);
    }

    /**
     * @param FromUserName
     * @param ToUserName
     * @param Content
     * @Description:封装发送消息对象,封装时，需要将调换发送者和接收者的关系
     */
    public String initMessage(String FromUserName, String ToUserName, String Content) {
        MessageText text = MessageText.builder()
                .toUserName(FromUserName)
                .fromUserName(ToUserName)
                .content("您输入的内容是：" + Content)
                .createTime(new Date().getTime())
                .msgType(OfficialMsgType.RESP_TEXT.getCode()).build();
        logger.debug("用户发送内容：{}", Content);
        return messageToxml(text);
    }
}
