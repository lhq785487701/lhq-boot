package com.lhq.superboot.service.impl;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lhq.superboot.service.OfficialApiService;
import com.lhq.superboot.service.OfficialEventHandleService;
import com.lhq.superboot.util.SpringContextUtil;

/**
 * @Description: 公众号事件处理接口
 * @author: lihaoqi
 * @date: 2019年9月10日 下午3:40:02
 * @version: v1.0.0
 */
@Service("OfficialEventHandleServiceImpl")
public class OfficialEventHandleServiceImpl implements OfficialEventHandleService {

    private static final Logger logger = LoggerFactory.getLogger(OfficialEventHandleService.class);

    @Autowired
    private OfficialApiService offApiService;

    @Override
    public String HandleEvent(String EventKey, String paramJson) {
        String message = null;
        try {
            Class<?> curClass = Class.forName(this.getClass().getName());
            Method event = curClass.getMethod(EventKey, String.class);

            message = (String) event.invoke(SpringContextUtil.getBean(this.getClass().getSimpleName()), paramJson);
        } catch (Exception e) {
            logger.debug("[HandleEvent] -> 反射事件报错，错误信息：{}", e.getMessage());
        }
        return message;
    }

    public String getPersonMessage(String openid) {
        return offApiService.getPersonalMsg(openid);
    }

}
