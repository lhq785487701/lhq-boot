package com.lhq.superboot.enums;

/**
 * @Description: 通知优先级
 * @author: lihaoqi
 * @date: 2019年7月5日 下午2:38:13
 * @version: v1.0.0
 */
public enum Noticepriority {

    NOT_URGENT(1),

    DEFAULT(2),

    URGENT(3),

    UNUSUALLY_URGENT(4);

    private Integer code;

    Noticepriority(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
