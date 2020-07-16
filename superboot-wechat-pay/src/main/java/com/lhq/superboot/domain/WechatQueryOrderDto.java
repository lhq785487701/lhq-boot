package com.lhq.superboot.domain;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 查询订单返回值
 * @author: lihaoqi
 * @date: 2019年7月8日 上午11:56:38
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WechatQueryOrderDto {

    /**
     * 查询订单返回的结果集
     **/
    private Map<String, String> result;

    /**
     * 查询订单结果(请对应OrderCode枚举的值)
     **/
    private Integer code;

    /**
     * 结果描述
     **/
    private String desc;

}
