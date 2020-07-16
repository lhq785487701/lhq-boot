package com.lhq.superboot.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 用户充值时传参
 * @author: lihaoqi
 * @date: 2019年7月9日 上午1:03:52
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RechargeVo {

    private BigDecimal token;
    private BigDecimal money;
    private String userId;
    private String wechatId;
    private String orderNo;
    private String detail;

}
