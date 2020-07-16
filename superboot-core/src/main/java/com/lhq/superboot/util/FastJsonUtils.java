package com.lhq.superboot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: fastjson工具类
 * @author: lihaoqi
 * @date: 2019年4月13日
 * @version: v1.0.0
 */
public class FastJsonUtils {

    private static final SerializeConfig CONFIG;

    static {
        CONFIG = new SerializeConfig();
    }

    private static final SerializerFeature[] FEATURES = {SerializerFeature.WriteMapNullValue, // 输出空置字段
            SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
            SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullStringAsEmpty, // 字符类型字段如果为null，输出为""，而不是null
            SerializerFeature.WriteBigDecimalAsPlain // 处理BigDecimal
    };

    private static final SerializerFeature[] NOFEATURES = {SerializerFeature.WriteBigDecimalAsPlain // 处理BigDecimal
    };

    public static String toJSONFeatures(Object object) {
        return JSON.toJSONString(object, CONFIG, FEATURES);
    }

    public static String toJSONNoFeatures(Object object) {
        return JSON.toJSONString(object, CONFIG, NOFEATURES);
    }

    public static Object toBean(String text) {
        return JSON.parse(text);
    }

    public static <T> T toBean(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    /**
     * 转换为数组
     **/
    public static <T> Object[] toArray(String text) {
        return toArray(text, null);
    }

    /**
     * 转换为数组
     **/
    public static <T> Object[] toArray(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz).toArray();
    }

    /**
     * 转换为List
     **/
    public static <T> List<T> toList(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz);
    }

    /**
     * @param text
     * @return
     * @Description: 将string转化为序列化的json字符串
     */
    public static Object textToJson(String text) {
        return JSON.parse(text);
    }

    /**
     * @param s
     * @return
     * @Description: json字符串转化为map
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> stringToCollect(String s) {
        return (Map<K, V>) JSONObject.parseObject(s);
    }

    /**
     * @param s
     * @return
     * @Description: json字符串转化为LinkedHashMap
     */
    @SuppressWarnings("unchecked")
    public static <K, V> LinkedHashMap<K, V> stringToLinkedHashMap(String s) {
        return JSON.parseObject(s, LinkedHashMap.class, Feature.OrderedField);
    }

    /**
     * @param jsonData
     * @param clazz
     * @return
     * @Description: 转换JSON字符串为对象
     */
    public static Object convertJsonToObject(String jsonData, Class<?> clazz) {
        return JSONObject.parseObject(jsonData, clazz);
    }

    /**
     * @param m
     * @return
     * @Description: 将map转化为string
     */
    public static <K, V> String collectToString(Map<K, V> m) {
        return JSONObject.toJSONString(m);
    }

}
