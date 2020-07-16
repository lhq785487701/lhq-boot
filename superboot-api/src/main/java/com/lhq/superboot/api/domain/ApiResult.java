package com.lhq.superboot.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 执行返回值
 * @author: lihaoqi
 * @date: 2019/12/23 10:30
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult {

    private String message;

    /**
     * 执行返回码
     **/
    private String result;

    /**
     * 返回数据
     **/
    private Object data;
}
