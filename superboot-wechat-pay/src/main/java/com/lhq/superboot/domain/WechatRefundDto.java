package com.lhq.superboot.domain;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 支付必要参数
 * @author: lihaoqi
 * @date: 2019年7月3日 下午11:14:54
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WechatRefundDto {

    /**
     * 退款结果(success/fail)
     **/
    private String refundResult;

    /**
     * 退款返回结果集(微信端返回数据)
     **/
    private Map<String, String> resultMap;

    /**
     * 退款结果说明(英文)
     **/
    private String resultDesc;


}
