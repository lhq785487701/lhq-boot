package com.lhq.superboot.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: list分页
 * @author: lihaoqi
 * @date: 2019年7月29日 下午4:51:49
 * @version: v1.0.0
 */
public class ListUtils {

    /**
     * @param <T>    原数组
     * @param source
     * @param maxLen 最大值，超过分组
     * @return 返回分组后的list
     * @Description: 将一组数据固定分组，每组n个元素
     */
    public static <T> List<List<?>> listGrouping(List<?> source, Integer maxLen) {
        if (null == source || source.isEmpty()) {
            return null;
        }
        List<List<?>> result = new ArrayList<>();
        int remainder = source.size() % maxLen;
        int size = (source.size() / maxLen);
        List<?> subset;

        for (int i = 0; i < size; i++) {
            subset = source.subList(i * maxLen, (i + 1) * maxLen);
            result.add(subset);
        }
        if (remainder > 0) {
            subset = source.subList(size * maxLen, size * maxLen + remainder);
            result.add(subset);
        }
        return result;
    }

}
