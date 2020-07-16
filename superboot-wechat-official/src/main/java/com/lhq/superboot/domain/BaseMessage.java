package com.lhq.superboot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 公众号回复基类
 * @author: lihaoqi
 * @date: 2019年9月6日 下午12:03:47
 * @version: v1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseMessage {

    protected String ToUserName;
    protected String FromUserName;
    protected long CreateTime;
    protected String MsgType;
}
