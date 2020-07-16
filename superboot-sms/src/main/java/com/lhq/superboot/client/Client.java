package com.lhq.superboot.client;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description: 连接阿里短信client
 * @author: lihaoqi
 * @date: 2019年7月29日 下午1:56:44
 * @version: v1.0.0
 */
@Component
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    /**
     * 固定属性地区、阿里AccessKey、阿里AccessKeySecret
     **/
    private static String REGIONID;
    private static String ACCESSKEYID;
    private static String ACCESSKEYSECRET;

    @Value("${alibaba.regionId}")
    public void setRegionId(String RegionId) {
        REGIONID = RegionId;
    }

    @Value("${alibaba.accessKeyId}")
    public void setAccessKeyId(String accessKeyId) {
        ACCESSKEYID = accessKeyId;
    }

    @Value("${alibaba.accessKeySecret}")
    public void setAccessKeySecret(String accessKeySecret) {
        ACCESSKEYSECRET = accessKeySecret;
    }

    private static IAcsClient acsClient = null;

    /**
     * @return
     * @Description: 获取一个IAcsClient对象
     */
    public static IAcsClient getInstance() {
        try {
            synchronized (Client.class) {
                if (acsClient == null) {
                    // 创建DefaultAcsClient实例并初始化
                    DefaultProfile profile = DefaultProfile.getProfile(REGIONID, ACCESSKEYID, ACCESSKEYSECRET);
                    acsClient = new DefaultAcsClient(profile);
                }
            }
        } catch (Exception e) {
            logger.error("实例化IAcsClient异常，错误信息：{}", e.getMessage());
        }
        return acsClient;
    }

}
