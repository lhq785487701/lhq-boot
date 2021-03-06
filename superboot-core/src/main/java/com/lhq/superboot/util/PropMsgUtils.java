package com.lhq.superboot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * @Description: 读取配置文件信息
 * @author: lihaoqi
 * @date: 2019年4月13日
 * @version: v1.0.0
 */
@Component
public class PropMsgUtils {

    @Resource
    private RedisUtils redisUtils;

    private static String expNamespace;

    private static PropMsgUtils propMsgUtils;

    @Value("${exception.namespace}")
    public void setExpNamespace(String expNamespace) {
        PropMsgUtils.expNamespace = expNamespace;
    }

    @PostConstruct
    public void init() {
        propMsgUtils = this;
        // 初始化的时候，将本类中的propMsgUtils赋值给静态的本类变量
        propMsgUtils.redisUtils = this.redisUtils;
    }

    /**
     * @param code
     * @param args
     * @return
     * @Description: 读取异常配置信息
     */
    public static String getExceptionMsg(String code, Object[] args) {
        Object propObj = propMsgUtils.redisUtils.get(expNamespace + code);
        if (null == propObj) {
            return null;
        }
        return MessageFormat.format((String) propObj, args);
    }

}
