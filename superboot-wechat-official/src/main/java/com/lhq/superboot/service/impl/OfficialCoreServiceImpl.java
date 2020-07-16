package com.lhq.superboot.service.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.lhq.superboot.enums.OfficialMsgType;
import com.lhq.superboot.service.OfficialCoreService;
import com.lhq.superboot.service.OfficialEventHandleService;
import com.lhq.superboot.service.OfficialMenuService;
import com.lhq.superboot.service.msg.BaseMessageService;
import com.lhq.superboot.util.XmlUtils;

/**
 * @Description: 微信公众号核心接口
 * @author: lihaoqi
 * @date: 2019年9月10日 上午11:19:16
 * @version: v1.0.0
 */
@Service
public class OfficialCoreServiceImpl implements OfficialCoreService {

    private static final Logger logger = LoggerFactory.getLogger(OfficialCoreService.class);

    @Autowired
    @Qualifier("textMessage")
    private BaseMessageService<?> textMsg;

    @Autowired
    @Qualifier("publicMessage")
    private BaseMessageService<?> publicMsg;

    @Autowired
    private OfficialMenuService menuService;

    @Autowired
    private OfficialEventHandleService eventService;

    @Override
    public String officialMessageHandle(HttpServletRequest req) {
        String message = null;

        try {
            // 将微信请求xml转为map格式，获取所需的参数
            Map<String, String> map = XmlUtils.xmlToMap(req);
            String toUserName = map.get("ToUserName"); // 接受方账号（公众号）
            String fromUserName = map.get("FromUserName"); // 发送方账号
            String msgType = map.get("MsgType"); // 消息类型
            logger.debug("[officialMessageHandel] -> 信息类型为{}", msgType);

            if (OfficialMsgType.REQ_TEXT.getCode().equals(msgType)) {
                logger.debug("[officialMessageHandel] -> 处理文本信息");
                String content = map.get("Content");

                if ("createMenu0318".equals(content)) {
                    logger.debug("[officialMessageHandel] -> 初始化微信菜单");
                    menuService.creatMenu(menuService.initMenu());
                }
                message = textMsg.initMessage(fromUserName, toUserName, content);
            } else if (OfficialMsgType.REQ_EVENT.getCode().equals(msgType)) {
                logger.debug("[officialMessageHandel] -> 处理事件");
                String eventType = map.get("Event");
                logger.debug("[officialMessageHandel] -> 事件类型为{}", eventType);

                if (OfficialMsgType.EVENT_SUBSCRIBE.getCode().equals(eventType)) {
                    logger.debug("[officialMessageHandel] -> 处理关注事件");
                    message = publicMsg.initMessage(fromUserName, toUserName, null);
                } else if (OfficialMsgType.EVENT_UNSUBSCRIBE.getCode().equals(eventType)) {
                    logger.debug("[officialMessageHandel] -> 处理取消关注事件");
                    message = publicMsg.initMessage(fromUserName, toUserName, "取消关注事件");
                } else if (OfficialMsgType.EVENT_CLICK.getCode().equals(eventType)) {
                    logger.debug("[officialMessageHandel] -> 处理点击事件");
                    String eventKey = map.get("EventKey");
                    message = publicMsg.initMessage(fromUserName, toUserName,
                            eventService.HandleEvent(eventKey, fromUserName));
                } else {
                    logger.debug("[officialMessageHandel] -> 处理其他事件");
                    message = publicMsg.initMessage(fromUserName, toUserName, null);
                }
            } else if (OfficialMsgType.REQ_IMAGE.getCode().equals(msgType)) {
                logger.debug("[officialMessageHandel] -> 处理图片信息");
                message = publicMsg.initMessage(fromUserName, toUserName, "这只是一张普通图片");
            } else if (OfficialMsgType.REQ_VOICE.getCode().equals(msgType)) {
                logger.debug("[officialMessageHandel] -> 处理语音信息");
                // 该字段在开启了语音识别会存在
                String recognition = map.get("Recognition");

                message = publicMsg.initMessage(fromUserName, toUserName, "这是一条语音信息，信息内容：" + recognition);
            } else {
                logger.debug("[officialMessageHandel] -> 处理其他信息");
                message = publicMsg.initMessage(fromUserName, toUserName, null);
            }

        } catch (Exception e) {
            logger.error("[officialMessageHandel] -> 消息处理异常， 异常信息{}", e.getMessage());
        }
        return message;
    }

}
