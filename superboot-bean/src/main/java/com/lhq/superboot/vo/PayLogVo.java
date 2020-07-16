package com.lhq.superboot.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.lhq.superboot.domain.PayLog;
import com.lhq.superboot.enums.ConstEnumUtils.IS_VAILD;
import com.lhq.superboot.util.BigDecimalUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @author: lihaoqi
 * @date: 2019年7月16日 下午3:19:46
 * @version: v1.0.0
 */

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PayLogVo {

    private String payId;
    private String outTradeNo;
    private BigDecimal payPrice;
    private String userId;
    private Date payTime;
    private String payType;
    private String bankType;
    private String isValid;

    public static PayLogVo convert(PayLog log) {
        PayLogVo payLogVo = new PayLogVo().toBuilder().bankType(log.getBankType()).userId(log.getUserId())
                .payType(log.getPayType()).payTime(log.getPayTime()).payId(log.getPayId())
                .outTradeNo(log.getOutTradeNo()).isValid(IS_VAILD.getValue(log.getIsValid()))
                .payPrice(BigDecimalUtils.toDecimal(log.getPayPrice())).build();
        return payLogVo;
    }

}
