package com.lhq.superboot.wechat.util;

import com.lhq.superboot.util.SHA1Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @Description: 微信公众平台请求算法工具类
 * @author: lihaoqi
 * @date: 2019年9月5日 下午5:28:28
 * @version: v1.0.0
 */
@Component
public class WeChatOfficialUtils {

    private static String TOKEN;

    @Value("${wechat.official.token}")
    public void setToken(String token) {
        TOKEN = token;
    }

    /**
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     * @Description: 验证签名的方法
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        List<String> params = new ArrayList<>();
        params.add(TOKEN);
        params.add(timestamp);
        params.add(nonce);
        // 1. 将token、timestamp、nonce三个参数进行字典序排序
        /*以前的方法
        Collections.sort(params, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });*/
        // params.sort((o1, o2) -> o1.compareTo(o2));
        params.sort(Comparator.naturalOrder());
        // 2.将三个参数字符串拼接成一个字符串进行sha1加密
        String temp = SHA1Utils.encode(params.get(0) + params.get(1) + params.get(2));
        // 3. 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return temp.equals(signature);
    }
}
