package com.lhq.superboot.service.impl;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.lhq.superboot.client.Client;
import com.lhq.superboot.domain.SMSSendResult;
import com.lhq.superboot.domain.SMSTemplate;
import com.lhq.superboot.enums.AliSMSSendCode;
import com.lhq.superboot.enums.EnvProfile;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.property.SMSProperty;
import com.lhq.superboot.service.SMSCodeService;
import com.lhq.superboot.util.CheckUtils;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.RedisUtils;
import com.lhq.superboot.util.SpringContextUtil;
import com.lhq.superboot.util.StringUtils;

/**
 * @Description:
 * @author: lihaoqi
 * @date: 2019年7月29日 上午11:30:56
 * @version: v1.0.0
 */
@Service
public class SMSCodeServiceImpl implements SMSCodeService {

    private static final Logger logger = LoggerFactory.getLogger(SMSCodeService.class);

    /**
     * 手机短信验证码
     **/
    private static final String CODE_KEY = "lhq:superboot:phone:code:";

    /**
     * 手机已发送短信用户(限制短信重复发送限制)
     **/
    private static final String REPEAT_KEY = "lhq:superboot:phone:code:repeat:";

    /**
     * 手机短信验证码失效时间
     **/
    private static final int CODE_EXPIRE = 120;

    /**
     * 手机短信验证码重复发送时间限制
     **/
    private static final int REPEAT_EXPIRE = 10;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SMSProperty smsProperty;

    @Override
    public void getSMSCode(String phone) {
        getSMSCode(phone, null);
    }

    @Override
    public void getSMSCode(String phone, SMSTemplate template) {
        if (StringUtils.isEmpty(phone)) {
            throw new SuperBootException("lhq-superboot-phone-0008");
        }
        if (!CheckUtils.isPhoneLegal(phone)) {
            throw new SuperBootException("lhq-superboot-phone-0007");
        }

        // 生成验证码
        String validCode = String.valueOf(new Random().nextInt(899999) + 100000);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(smsProperty.getSmsDomain());
        request.setSysVersion(smsProperty.getSmsVersion());
        request.setSysAction(smsProperty.getSmsSendCode());

        // 封装验证码参数
        if (template == null) {
            // 如果template为空，使用默认的
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(smsProperty.getSmsParamCode(), validCode);
            request.putQueryParameter("TemplateCode", smsProperty.getSmsParamCode());
            request.putQueryParameter("TemplateParam", FastJsonUtils.toJSONFeatures(jsonObject));
        } else {
            // 有且仅有一个参数是验证码
            Map<String, Object> paramName = FastJsonUtils.stringToCollect(template.getTemplateParamName());
            Set<String> nameSet = paramName.keySet();
            if (nameSet.size() != 1) {
                throw new SuperBootException("lhq-superboot-sms-0009", nameSet);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(nameSet.iterator().next(), validCode);
            request.putQueryParameter("TemplateCode", template.getTemplateCode());
            request.putQueryParameter("TemplateParam", FastJsonUtils.toJSONFeatures(jsonObject));
        }

        // 传入必要参数
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", smsProperty.getSmsSignName());

        // 非正式环境使用不发送阿里云
        if (!SpringContextUtil.getActiveProfile().equals(EnvProfile.PROFILE_PROD.getCode())) {
            logger.debug("[SMSCodeService] -> 测试环境发送手机验证码:{}", validCode);
            setCodeInCache(phone, validCode);
            return;
        }

        try {
            logger.debug("[SMSCodeService] -> 发送手机验证码:{}", validCode);
            CommonResponse response = Client.getInstance().getCommonResponse(request);
            SMSSendResult result = FastJsonUtils.toBean(response.getData(), SMSSendResult.class);

            logger.debug("[SMSCodeService] -> 获取手机验证码结果：{}", result);
            if (!result.getCode().equals(AliSMSSendCode.OK.getCode())) {
                throw new Exception(AliSMSSendCode.getValue(result.getCode()));
            }
            setCodeInCache(phone, validCode);
        } catch (ServerException e) {
            logger.error("[SMSCodeService] -> 发送手机短信服务端报错：{}", e.getMessage());
            throw new SuperBootException(e);
        } catch (ClientException e) {
            logger.error("[SMSCodeService] -> 发送手机短信服务端报错：{}", e.getMessage());
            throw new SuperBootException(e);
        } catch (Exception e) {
            logger.error("[SMSCodeService] -> 发送手机短信错误：{}", e.getMessage());
            throw new SuperBootException(e);
        }
    }

    @Override
    public boolean setCodeInCache(String phone, String validCode) {
        try {
            redisUtils.set(CODE_KEY + phone, validCode, CODE_EXPIRE);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validateCode(String phone, String validCode) {
        Object code = redisUtils.get(CODE_KEY + phone);
        if (null == code) {
            throw new SuperBootException("lhq-superboot-phone-0001");
        }
        if (!code.toString().equals(validCode)) {
            throw new SuperBootException("lhq-superboot-phone-0002");
        }
        redisUtils.del(CODE_KEY + phone);
        return true;
    }

    @Override
    public boolean validateCodeRepeatSend(String phone) {
        Object code = redisUtils.get(REPEAT_KEY + phone);
        if (null != code) {
            return false;
        } else {
            redisUtils.set(REPEAT_KEY + phone, phone, REPEAT_EXPIRE);
            return true;
        }
    }

}
