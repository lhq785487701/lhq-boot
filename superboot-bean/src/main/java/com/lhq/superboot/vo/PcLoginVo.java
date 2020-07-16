package com.lhq.superboot.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: pc端登录vo
 * @author: lihaoqi
 * @version: 1.0.0
 * @date: 2019年4月24日
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PcLoginVo {

    private String password;

    private String userName;

    private String phone;

    private String validCode;

    private String channelFlg;

    private String loginMode;

}
