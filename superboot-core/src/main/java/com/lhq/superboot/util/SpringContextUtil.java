package com.lhq.superboot.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @Description: 获取bean的工具类，可用于在线程里面获取bean
 * @author: lihaoqi
 * @date: 2019年8月27日 下午3:46:06
 * @version: v1.0.0
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context = null;

    /*
     * @Description: spring获取bean工具类
     *
     * @param applicationContext
     * @throws BeansException
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @SuppressWarnings("static-access")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.context = applicationContext;
    }

    /**
     * @param <T>
     * @param beanName
     * @return
     * @Description: 传入线程中
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        return (T) context.getBean(beanName);
    }

    public static Object getBean(String name, Class<?> cla) throws BeansException {
        return context.getBean(name, cla);
    }

    /**
     * @param <T>
     * @param requiredType
     * @return
     * @Description: 获取某个类型下的所有请求方法
     */
    public static <T> T getBean(Class<T> requiredType) {
        return context.getBean("requestMappingHandlerMapping", requiredType);
    }

    /**
     * @param key
     * @return
     * @Description: 国际化使用
     */
    public static String getMessage(String key) {
        return context.getMessage(key, null, Locale.getDefault());
    }

    /**
     * @return
     * @Description: 获取当前环境
     */
    public static String getActiveProfile() {
        return context.getEnvironment().getProperty(StandardEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
    }
}
