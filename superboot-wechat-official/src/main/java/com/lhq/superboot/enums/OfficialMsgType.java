package com.lhq.superboot.enums;

/**
 * @Description: 公众号消息类型
 * @author: lihaoqi
 * @date: 2019年4月24日
 * @version: 1.0.0
 */
public enum OfficialMsgType {

    /**
     * 请求消息类型
     **/
    REQ_TEXT("text", "文本"),

    REQ_IMAGE("image", "图片"),

    REQ_VOICE("voice", "语音"),

    REQ_VIDEO("video", "视频"),

    REQ_SHORTVIDEO("shortvideo", "小视频"),

    REQ_LINK("link", "链接"),

    REQ_LOCATION("location", "地理位置"),

    REQ_EVENT("event", "事件推送"),

    /**
     * 回复消息类型
     **/
    RESP_TEXT("text", "文本"),

    RESP_IMAGE("image", "图片"),

    RESP_VOICE("voice", "语音"),

    RESP_MUSIC("music", "音乐"),

    RESP_NEWS("news", "图文"),

    RESP_VIDEO("video", "视频"),

    /**
     * 事件消息类型
     **/
    EVENT_SUBSCRIBE("subscribe", "事件类型:订阅"),

    EVENT_UNSUBSCRIBE("unsubscribe", "事件类型:取消订阅"),

    EVENT_LOCATION("location", "地理位置"),

    EVENT_CLICK("CLICK", "CLICK(点击菜单拉取消息)"),

    EVENT_VIEW("VIEW", "事件类型：VIEW(自定义菜单URl视图)");

    private final String value;
    private final String code;

    OfficialMsgType(String code, String value) {
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
        OfficialMsgType[] wechatErrCodeEnums = values();
        for (OfficialMsgType wechatErrCodeEnum : wechatErrCodeEnums) {
            if (wechatErrCodeEnum.code.equals(code)) {
                return wechatErrCodeEnum.value;
            }
        }
        return null;
    }
}
