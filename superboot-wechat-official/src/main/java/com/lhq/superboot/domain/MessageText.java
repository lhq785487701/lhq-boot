package com.lhq.superboot.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Description: 消息内容实体
 * @author: lihaoqi
 * @date: 2019年9月6日 下午12:05:02
 * @version: v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MessageText extends BaseMessage {

    private String Content;// 文本消息内容

    private String MsgId;// 消息id，64位整型

    public MessageText(String content, String msgId) {

    }

    @Builder
    public MessageText(String toUserName, String fromUserName, long createTime, String msgType, String content,
                       String msgId) {
        super(toUserName, fromUserName, createTime, msgType);
        this.Content = content;
        this.MsgId = msgId;
    }

}
