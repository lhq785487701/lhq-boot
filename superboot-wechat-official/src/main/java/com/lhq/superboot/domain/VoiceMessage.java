package com.lhq.superboot.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Description: 语音消息实体类
 * @author: lihaoqi
 * @date: 2019年9月6日 下午12:05:02
 * @version: v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class VoiceMessage extends BaseMessage {

    private long MediaId;

    private String Format;// 格式

    private String MsgId;// 消息id，64位整型

    public VoiceMessage(long mediaId, String format, String msgId) {

    }

    @Builder
    public VoiceMessage(String toUserName, String fromUserName, long createTime, long mediaId, String msgType, String format,
                        String msgId) {
        super(toUserName, fromUserName, createTime, msgType);
        this.MediaId = mediaId;
        this.Format = format;
        this.MsgId = msgId;
    }

}
