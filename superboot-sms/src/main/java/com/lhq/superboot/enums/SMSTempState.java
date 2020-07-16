package com.lhq.superboot.enums;

/**
 * @Description: 短信模板审核的状态
 * @author: lihaoqi
 * @date: 2019年7月29日 下午12:33:47
 * @version: v1.0.0
 */
public enum SMSTempState {

    AUDIT_PASS(1, "审核通过"),

    AUDIT_NOT_PASS(2, "审核未通过"),

    AUDITING(3, "审核中");

    private Integer code;
    private String desc;

    SMSTempState(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescription(Integer value) {
        SMSTempState[] enums = values();
        for (SMSTempState entity : enums) {
            if (entity.getCode().equals(value)) {
                return entity.getDesc();
            }
        }
        return null;
    }
}
