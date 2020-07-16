package com.lhq.superboot.api.utils;

import com.lhq.superboot.api.domain.ApiEntity;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Description: api加密工具类
 * @author: lihaoqi
 * @date: 2019/12/23 13:43
 * @version: v1.0.0
 */
public class ApiSignUtils {

    private static final Logger logger = LoggerFactory.getLogger(ApiSignUtils.class);

    private ApiSignUtils() {
        throw new IllegalStateException("ApiSignUtils class");
    }


    /**
     * @param apiEntity
     * @param apiSecret
     * @return
     * @Description: 生成sign
     */
    public static String generatorSign(ApiEntity apiEntity, String apiSecret) {
        Map<String, String> map = new HashMap<>();
        map.put("execcode", apiEntity.getExeccode());
        map.put("apikey", apiEntity.getApikey());
        map.put("timestamp", apiEntity.getTimestamp());
        map.put("version", apiEntity.getVersion());
        return createApiSign(map, apiSecret);
    }

    /**
     * @param param
     * @param apiSecret
     * @return
     * @Description: 生成sign
     */
    private static String createApiSign(Map<String, String> param, String apiSecret) {
        Set<String> keySet = param.keySet();
        String[] keyArray = keySet.toArray(new String[0]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyArray.length; i++) {
            if (i != keyArray.length - 1) {
                sb.append(keyArray[i]).append("=").append((param.get(keyArray[i])).trim()).append("&");
            } else {
                sb.append(keyArray[i]).append("=").append((param.get(keyArray[i])).trim());
            }
        }
        logger.debug("[ApiSignUtils] -> 签名参数" + FastJsonUtils.toJSONFeatures(sb));

        try {
            String sign = MD5Utils.MD5(MD5Utils.MD5(sb.toString()) + apiSecret);
            logger.debug("[ApiSignUtils] -> 签名信息：{}", sign);
            return sign;
        } catch (Exception e) {
            logger.debug("[ApiSignUtils] -> 签名失败{}", e.getMessage());
            return null;
        }
    }
}
