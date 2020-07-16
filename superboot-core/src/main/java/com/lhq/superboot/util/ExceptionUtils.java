package com.lhq.superboot.util;

import com.lhq.superboot.exception.SuperBootException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @Description: 异常处理工具
 * @author: lihaoqi
 * @date: 2019年4月13日
 * @version: v1.0.0
 */
public class ExceptionUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionUtils.class);

    /**
     * @param msg
     * @param t
     * @return
     * @Description: 拼凑异常堆栈信息
     */
    public static StringBuilder getException(String msg, Throwable t) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(msg)) {
            sb.append(msg).append("\r");
        }
        StringBuilder sbStr = getException(t);
        sb.append(sbStr);
        return sb;
    }

    /**
     * @param t
     * @return
     * @Description: 获取异常堆栈信息
     */
    public static StringBuilder getException(Throwable t) {
        StringBuilder sb = new StringBuilder();
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bas);
        t.printStackTrace(ps);
        // 全部堆栈bas.toByteArray()
        // 简洁的错误信息
        sb.append(t.toString());
        ps.close();
        try {
            bas.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return sb;
    }

    /**
     * @Description: 循环获取异常信息（获取最内部的错误）
     */
    public static String getExceptionMsg(Throwable e) {
        while (e != null) {
            if (e instanceof SuperBootException) {
                return e.getMessage();
            }
            Throwable tex = e.getCause();
            if (tex == null) {
                return e.getMessage();
            } else {
                e = tex;
            }
        }
        return null;
    }
}
