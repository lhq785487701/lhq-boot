package com.lhq.superboot.api.enums;

/**
 * @Description: 执行结果
 * @author: lihaoqi
 * @date: 2019年4月25日
 * @version: 1.0.0
 */
public enum ExecResult {

    SUCCESS("success"),

    ERROR("error");

    private String code;

    ExecResult(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
