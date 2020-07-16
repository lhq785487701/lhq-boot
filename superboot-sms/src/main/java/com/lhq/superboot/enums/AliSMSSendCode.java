package com.lhq.superboot.enums;

/**
 * @Description: 短信接口调用返回的code
 * @author: lihaoqi
 * @date: 2019年4月29日
 * @version: 1.0.0
 */
public enum AliSMSSendCode {

    OK("OK", "正常返回"),

    SMS_TEMPLATE_ILLEGAL("isv.SMS_TEMPLATE_ILLEGAL", "模板不合法(不存在或被拉黑)"),

    SMS_SIGNATURE_ILLEGAL("isv.SMS_SIGNATURE_ILLEGAL", "签名不合法(不存在或被拉黑)"),

    MOBILE_NUMBER_ILLEGAL("isv.MOBILE_NUMBER_ILLEGAL", "无效号码"),

    TEMPLATE_MISSING_PARAMETERS("isv.TEMPLATE_MISSING_PARAMETERS", "模板变量缺少对应参数值"),

    EXTEND_CODE_ERROR("isv.EXTEND_CODE_ERROR", "扩展码使用错误"),

    DOMESTIC_NUMBER_NOT_SUPPORTED("isv.DOMESTIC_NUMBER_NOT_SUPPORTED", "国际/港澳台消息模板不支持发送境内号码"),

    DAY_LIMIT_CONTROL("isv.DAY_LIMIT_CONTROL", "触发日发送限额"),

    SMS_CONTENT_ILLEGAL("isv.SMS_CONTENT_ILLEGAL", "短信内容包含禁止发送内容"),

    SMS_SIGN_ILLEGAL("isv.SMS_SIGN_ILLEGAL", "签名禁止使用"),

    RAM_PERMISSION_DENY("isp.RAM_PERMISSION_DENY", "RAM权限DENY"),

    OUT_OF_SERVICE("isp.OUT_OF_SERVICE", "业务停机"),

    PRODUCT_UN_SUBSCRIPT("isv.PRODUCT_UN_SUBSCRIPT", "未开通云通信产品的阿里云客户"),

    PRODUCT_UNSUBSCRIBE("isv.PRODUCT_UNSUBSCRIBE", "产品未开通"),

    ACCOUNT_NOT_EXISTS("isv.ACCOUNT_NOT_EXISTS", "账户不存在"),

    BUSINESS_LIMIT_CONTROL("isv.BUSINESS_LIMIT_CONTROL", "该号码获取验证码次数已上限");

    private final String value;
    private final String code;

    AliSMSSendCode(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static String getValue(String code) {
        AliSMSSendCode[] aliSMSSendCodes = values();
        for (AliSMSSendCode aliSMSSendCode : aliSMSSendCodes) {
            if (aliSMSSendCode.code.equals(code)) {
                return aliSMSSendCode.value;
            }
        }
        return null;
    }
}
