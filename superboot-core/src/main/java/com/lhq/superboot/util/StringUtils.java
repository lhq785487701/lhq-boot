package com.lhq.superboot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 字符串处理工具
 * @author: lihaoqi
 * @date: 2019年4月13日
 * @version: v1.0.0
 */
public class StringUtils extends org.springframework.util.StringUtils {

    private static final String MESS_STR = "\\s*|t*| r*|n*";

    /**
     * @param str
     * @return
     * @Description: 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * @param s 原字串,如为null,返回空串.
     * @return 去掉两边空格的字串
     * @Description: 去掉字串两边空格
     */
    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    /**
     * @param c 字符
     * @return 是否是中文
     * @Description: 判断字符是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * @param strName 字符串
     * @return 是否是乱码
     * @Description: 判断字符串是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile(MESS_STR);
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }
        float result = count / chLength;
        return result > 0.4;
    }
}
