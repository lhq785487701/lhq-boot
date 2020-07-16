package com.lhq.superboot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: lihaoqi
 * @date: 2019年4月24日
 * @version: 1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WechatAuthResult {

    private String openid;
    private String sessionKey;
    private String unionid;
    private int errcode;
    private String errmsg;
}
