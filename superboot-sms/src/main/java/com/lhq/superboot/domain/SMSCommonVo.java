package com.lhq.superboot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 公共发送短信传值
 * @author: lihaoqi
 * @date: 2019年7月29日 上午11:56:45
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SMSCommonVo {

    /**
     * 需要发送短信的手机号
     **/
    private String phone;

    /**
     * 发送短信的参数json格式{name：值}
     **/
    private String param;
}
