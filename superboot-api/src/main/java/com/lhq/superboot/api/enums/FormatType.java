package com.lhq.superboot.api.enums;

/**
 * @Description: 参数类型
 * @author: lihaoqi
 * @date: 2019年4月25日
 * @version: 1.0.0
 */
public enum FormatType {

    JSON("json"),

    XML("xml");

    private String code;

    FormatType(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
