package com.lhq.superboot.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 微信获取openid的vo
 * @author: lihaoqi
 * @date: 2019年4月24日
 * @version: 1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WechatLoginVo {

    private String code;

    private String phone;

    private String validCode;

    private String token;

}
