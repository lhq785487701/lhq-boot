package com.lhq.superboot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: 时间转化工具类
 * @author: lihaoqi
 * @date: 2019年11月4日 下午12:08:29
 * @version: v1.0.0
 */
public class DateConvertUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateConvertUtils.class);

    // 这里初始化喜欢使用的日期和时间格式，这里保持和系统一致
    private final static String dateStr = "yyyy-MM-dd";
    private final static String timeStr = "HH:mm:ss";
    private final static String dateTimeStr = dateStr + " " + timeStr;
    private final static String preHints = "参数格式错误：";

    public static final DateFormat getDateFormat() {
        return new SimpleDateFormat(dateStr);
    }

    public static final DateFormat getTimeFormat() {
        return new SimpleDateFormat(timeStr);
    }

    public static final DateFormat getDateTimeFormat() {
        return new SimpleDateFormat(dateTimeStr);
    }

    // 当前日期
    public static String nowDate() {
        return getDateFormat().format(System.currentTimeMillis());
    }

    // 日期格式化输出
    public static String dateToString(Date date) {
        return getDateFormat().format(date);
    }

    // 当前时间
    public static String nowTime() {
        return getTimeFormat().format(System.currentTimeMillis());
    }

    // 时间格式化输出
    public static String timeToString(Date date) {
        return getTimeFormat().format(date);
    }

    // 当前日期和时间
    public static String nowDateTime() {
        return getDateTimeFormat().format(System.currentTimeMillis());
    }

    // 日期时间格式化输出
    public static String dateTimeToString(Date date) {
        return getDateTimeFormat().format(date);
    }

    // 按照传入格式输出String时间
    public static String dateTimeToString(Date date, String dateTimeStr) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateTimeStr);
        return dateTimeFormat.format(date);
    }

    // 串到日期
    public static Date toDate(String date) {
        Date result = null;
        try {
            result = getDateFormat().parse(date);
        } catch (ParseException e) {
            logger.error(preHints + dateStr);
        }
        return result;
    }

    // 串到时间
    public static Date toTime(String time) {
        Date result = null;
        try {
            result = getTimeFormat().parse(time);
        } catch (ParseException e) {
            logger.error(preHints + dateStr);
        }
        return result;
    }

    // 串到日期加时间yyyy-MM-dd HH:mm:ss
    public static Date toDateTime(String time) {
        Date result = null;
        try {
            result = getDateTimeFormat().parse(time);
        } catch (ParseException e) {
            logger.error(preHints + dateTimeStr);
        }
        return result;
    }

    public static Date toDateTime(String time, String dateTimeStr) {
        Date result = null;
        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateTimeStr);
            result = dateTimeFormat.parse(time);
        } catch (ParseException e) {
            logger.error(preHints + dateTimeStr);
        }
        return result;
    }

}
