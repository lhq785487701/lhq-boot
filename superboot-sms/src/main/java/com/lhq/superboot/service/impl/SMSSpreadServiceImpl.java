package com.lhq.superboot.service.impl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.lhq.superboot.client.Client;
import com.lhq.superboot.domain.SMSSendResult;
import com.lhq.superboot.enums.AliSMSSendCode;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.property.SMSProperty;
import com.lhq.superboot.service.SMSSpreadService;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.ListUtils;
import com.lhq.superboot.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 短信推广实现类
 * @author: lihaoqi
 * @date: 2019年7月29日 上午11:30:56
 * @version: v1.0.0
 */
@Service
public class SMSSpreadServiceImpl implements SMSSpreadService {

    private static final Logger logger = LoggerFactory.getLogger(SMSSpreadService.class);

    /**
     * 批量发送(SendSms)手机个数最大值
     **/
    private static final int PHONE_MAX_LEN = 1000;

    @Autowired
    private SendSMSThread sendSMSThread;

    @Override
    @SuppressWarnings("unchecked")
    public void sendSMSSpread(List<String> phoneList, String templateCode) {
        if (phoneList.isEmpty()) {
            logger.error("[SMSSpreadService] -> 发送手机列表为空");
            throw new SuperBootException("lhq-superboot-phone-0008");
        }
        if (StringUtils.isEmpty(templateCode)) {
            logger.error("[SMSSpreadService] -> 模板代码为空");
            throw new SuperBootException("lhq-superboot-sms-0001");
        }

        // 传入的手机参数，以逗号隔开(见阿里云短信说明)
        String phoneStr = "";
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        if (phoneList.size() > PHONE_MAX_LEN) {
            List<List<?>> sendListGroup = ListUtils.listGrouping(phoneList, PHONE_MAX_LEN);
            for (List<?> sendList : sendListGroup) {
                phoneStr = String.join(",", (List<String>) sendList);
                fixedThreadPool.execute(sendSMSThread.toBuilder().phoneStr(phoneStr).templateCode(templateCode).build());
            }
        } else {
            phoneStr = String.join(",", (List<String>) phoneList);
            fixedThreadPool.execute(sendSMSThread.toBuilder().phoneStr(phoneStr).templateCode(templateCode).build());
        }
    }

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Component
    private static class SendSMSThread implements Runnable {

        private String phoneStr;
        private String templateCode;

        @Autowired
        private SMSProperty smsProperty;

        @Override
        public void run() {
            CommonRequest request = new CommonRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysDomain(smsProperty.getSmsDomain());
            request.setSysVersion(smsProperty.getSmsVersion());
            request.setSysAction(smsProperty.getSmsSendCode());

            // 传入必要参数
            request.putQueryParameter("PhoneNumbers", phoneStr);
            request.putQueryParameter("TemplateCode", templateCode);
            request.putQueryParameter("SignName", smsProperty.getSmsSignName());

            try {
                logger.debug("[SMSSpreadService] -> 发送手机短信推广");
                CommonResponse response = Client.getInstance().getCommonResponse(request);
                SMSSendResult result = FastJsonUtils.toBean(response.getData(), SMSSendResult.class);

                if (!result.getCode().equals(AliSMSSendCode.OK.getCode())) {
                    throw new Exception(AliSMSSendCode.getValue(result.getCode()));
                }
            } catch (ServerException e) {
                logger.error("[SMSSpreadService] -> 发送手机短信服务端报错：{}", e.getMessage());
                throw new SuperBootException(e);
            } catch (ClientException e) {
                logger.error("[SMSSpreadService] -> 发送手机短信服务端报错：{}", e.getMessage());
                throw new SuperBootException(e);
            } catch (Exception e) {
                logger.error("[SMSSpreadService] -> 发送手机短信错误：{}", e.getMessage());
                throw new SuperBootException(e);
            }
        }

    }

}
