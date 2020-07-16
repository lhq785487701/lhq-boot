package com.lhq.superboot.api.enums;

/**
 * @Description: 是否异步请求
 * @author: lihaoqi
 * @date: 2019年4月25日
 * @version: 1.0.0
 */
public enum AsynType {

    NOT_ASYN(0),

    ASYN(1);

    private int code;

    AsynType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
