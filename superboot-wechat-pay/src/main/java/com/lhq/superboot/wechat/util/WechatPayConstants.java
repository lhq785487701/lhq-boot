package com.lhq.superboot.wechat.util;

import com.lhq.superboot.enums.EnvProfile;
import com.lhq.superboot.util.SpringContextUtil;

/**
 * @Description: 微信使用的常量
 * @author: lihaoqi
 * @date: 2019年7月4日 上午11:28:28
 * @version: v1.0.0
 */
public final class WechatPayConstants {

    /**
     * notifyUrl
     **/
    public static final String RECHARGE_NOTIFY_URL = SpringContextUtil.getActiveProfile().equals(EnvProfile.PROFILE_PROD.getCode()) ?
            "https://www.xxxx.com/superboot/user/recharge/notify" : "https://www.xxxx.com/superboot-test/user/recharge/notify";
    public static final String GOODS_PAY_NOTIFY_URL = SpringContextUtil.getActiveProfile().equals(EnvProfile.PROFILE_PROD.getCode()) ?
            "https://www.xxxx.com/superboot/order/pay/notify" : "https://www.xxxx.com/superboot-test/order/pay/notify";

    /**
     * wechatNotify
     **/
    public static final String SUCCESS = "<xml><return_code>SUCCESS</return_code><return_msg>OK</return_msg></xml>";
    public static final String SIGN_FAIL = "<xml><return_code>FAIL</return_code><return_msg>签名不一致</return_msg></xml>";
    public static final String NOTIFY_FAIL = "<xml><return_code>FAIL</return_code><return_msg>支付通知失败</return_msg></xml>";
    public static final String MONEY_FAIL = "<xml><return_code>FAIL</return_code><return_msg>支付金额不一致后</return_msg></xml>";
}
