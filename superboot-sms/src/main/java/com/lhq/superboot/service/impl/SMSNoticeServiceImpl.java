package com.lhq.superboot.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.lhq.superboot.client.Client;
import com.lhq.superboot.domain.SMSCommonVo;
import com.lhq.superboot.domain.SMSSendLog;
import com.lhq.superboot.domain.SMSSendResult;
import com.lhq.superboot.domain.SMSTemplate;
import com.lhq.superboot.enums.AliSMSSendCode;
import com.lhq.superboot.enums.SMSSendState;
import com.lhq.superboot.enums.SMSTempType;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.property.SMSProperty;
import com.lhq.superboot.service.SMSLogService;
import com.lhq.superboot.service.SMSNoticeService;
import com.lhq.superboot.util.CheckUtils;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.ListUtils;
import com.lhq.superboot.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 短信通知实现类
 * @author: lihaoqi
 * @date: 2019年7月29日 下午4:56:19
 * @version: v1.0.0
 */
@Service
public class SMSNoticeServiceImpl implements SMSNoticeService {

    private static final Logger logger = LoggerFactory.getLogger(SMSNoticeService.class);

    /**
     * 批量发送(SendBatchSms)手机个数最大值
     **/
    private static final int PHONE_BATCH_MAX_LEN = 100;

    @Autowired
    private SendSMSThread sendSMSThread;

    @Override
    public void sendSMSNotice(List<SMSCommonVo> paramList) {
        sendSMSNotice(paramList, null, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sendSMSNotice(List<SMSCommonVo> paramList, SMSTemplate template, boolean isDefault) {
        // 校验手机是否合法
        Iterator<SMSCommonVo> it = paramList.iterator();
        while (it.hasNext()) {
            SMSCommonVo smsCommonVo = it.next();
            String phone = smsCommonVo.getPhone();
            if (!CheckUtils.isPhoneLegal(phone)) {
                // 需要记录下来
                logger.error("[SMSNoticeService] -> 手机号为：{}非合法手机号，无法发送短信通知", phone);
                it.remove();
            }
        }
        // 可能没有合法的
        if (paramList.isEmpty()) {
            logger.error("[SMSNoticeService] -> 不存在合法的手机号发送信息");
            return;
        }

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        if (paramList.size() > PHONE_BATCH_MAX_LEN) {
            List<List<?>> sendListGroup = ListUtils.listGrouping(paramList, PHONE_BATCH_MAX_LEN);
            for (List<?> sendList : sendListGroup) {
                fixedThreadPool.execute(sendSMSThread.toBuilder().paramList((List<SMSCommonVo>) sendList)
                        .template(template).isDefault(isDefault).build());
            }
        } else {
            fixedThreadPool.execute(
                    sendSMSThread.toBuilder().paramList(paramList).template(template).isDefault(isDefault).build());
        }
    }

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @Component
    private static class SendSMSThread implements Runnable {

        private List<SMSCommonVo> paramList;
        private SMSTemplate template;
        private boolean isDefault;

        @Autowired
        private SMSProperty smsProperty;

        @Autowired
        private SMSLogService smsLogService;

        @Override
        public void run() {
            // 判断参数中手机号是否存在重复，允许重复手机号发送，不过需要单条发送
            boolean hasPhoneRepeat = checkPhoneRepeat();
            // 使用默认模板，使用配置文件中的固定属性
            if (isDefault) {
                // 存在重复手机号需要一条一条发送，否则批量发送只发送一条短信
                if (hasPhoneRepeat) {
                    sendSmsByDefault();
                } else {
                    sendBatchSmsByDefault();
                }
                return;
            }

            try {
                /** 参数名(可为空) **/
                Set<Object> paramNames = null;
                /** 固定参数(可为空) **/
                Map<String, Object> fixParams = null;
                /** 模板code(不可为空) **/
                String templateCode = template.getTemplateCode();
                if (StringUtils.isNotEmpty(template.getTemplateParamName())) {
                    paramNames = FastJsonUtils.stringToCollect(template.getTemplateParamName()).keySet();
                }
                if (StringUtils.isNotEmpty(template.getTemplateFixParam())) {
                    fixParams = FastJsonUtils.stringToCollect(template.getTemplateFixParam());
                }
                if (StringUtils.isEmpty(templateCode)) {
                    throw new SuperBootException("exp-superboot-sms-0001");
                }
                // 存在重复手机号需要一条一条发送，否则批量发送只发送一条短信
                if (hasPhoneRepeat) {
                    sendSms(paramNames, fixParams, templateCode);
                } else {
                    sendBatchSms(paramNames, fixParams, templateCode);
                }
            } catch (Exception e) {
                logger.error("[SMSNoticeService] -> 获取模板参数异常,错误信息：{}", e.getMessage());
            }
        }

        /**
         * -------------------------------------sendBatchSms-------------------------------------------
         **/

        /**
         * @Description: 批量发送短信、使用默认模板只有三个参数content name count
         */
        private void sendBatchSmsByDefault() {
            // 三个参数，手机的json、参数的json、签名的json
            JSONArray phoneJsonArray = new JSONArray();
            JSONArray paramJsonArray = new JSONArray();
            JSONArray signJsonArray = new JSONArray();

            // sign固定
            String sign = smsProperty.getSmsSignName();

            for (SMSCommonVo param : paramList) {
                String phone = param.getPhone();
                JSONObject paramJsonObject = new JSONObject(3);
                try {
                    // 封装传入变量
                    Map<String, Object> paramMap = FastJsonUtils.stringToCollect(param.getParam());
                    paramJsonObject.put(smsProperty.getSmsParamContent(),
                            paramMap.get(smsProperty.getSmsParamContent()));
                    paramJsonObject.put(smsProperty.getSmsParamName(), paramMap.get(smsProperty.getSmsParamName()));
                    paramJsonObject.put(smsProperty.getSmsParamCount(), paramMap.get(smsProperty.getSmsParamCount()));
                } catch (Exception e) {
                    logger.error("[SMSNoticeService] -> 手机号为{}的用户获取发送短信参数异常,参数{}, 错误信息：{}", param.getPhone(),
                            param.getParam(), e.getMessage());
                    continue;
                }
                phoneJsonArray.add(phone);
                signJsonArray.add(sign);
                paramJsonArray.add(paramJsonObject);
            }

            String templateCode = smsProperty.getSmsGiveCardTemplateCode();
            String paramJson = FastJsonUtils.toJSONFeatures(paramJsonArray);
            String phoneJson = FastJsonUtils.toJSONFeatures(phoneJsonArray);
            String signJson = FastJsonUtils.toJSONFeatures(signJsonArray);
            sendBatchSmsRequest(templateCode, phoneJson, paramJson, signJson);
        }

        /**
         * @param paramNames
         * @param fixParams
         * @param templateCode
         * @Description: 批量发送短信
         */
        private void sendBatchSms(Set<Object> paramNames, Map<String, Object> fixParams, String templateCode) {
            // 三个参数，手机的json、参数的json、签名的json
            JSONArray phoneJsonArray = new JSONArray();
            JSONArray paramJsonArray = new JSONArray();
            JSONArray signJsonArray = new JSONArray();

            // 签名
            String sign = smsProperty.getSmsSignName();

            for (SMSCommonVo param : paramList) {
                String phone = param.getPhone();
                JSONObject paramJsonObject = null;
                try {
                    // 参数名列表
                    if (paramNames != null) {
                        // 封装传入变量
                        Map<String, Object> paramMap = FastJsonUtils.stringToCollect(param.getParam());
                        paramJsonObject = getSmsParamJsonObject(paramNames, fixParams, paramMap);
                    }
                } catch (Exception e) {
                    logger.error("[SMSNoticeService] -> 手机号为{}的用户获取发送短信参数异常,参数{}, 错误信息：{}", param.getPhone(),
                            param.getParam(), e.getMessage());
                    continue;
                }
                phoneJsonArray.add(phone);
                signJsonArray.add(sign);
                paramJsonArray.add(paramJsonObject);
            }
            if (phoneJsonArray.isEmpty()) {
                logger.error("[SMSNoticeService] -> 没有需要发送短信的手机列表");
                return;
            }

            String paramJson = FastJsonUtils.toJSONFeatures(paramJsonArray);
            String phoneJson = FastJsonUtils.toJSONFeatures(phoneJsonArray);
            String signJson = FastJsonUtils.toJSONFeatures(signJsonArray);
            sendBatchSmsRequest(templateCode, phoneJson, paramJson, signJson);
        }

        /**
         * @param templateCode
         * @param phoneJson
         * @param paramJson
         * @param signJson
         * @Description: 将参数写入并请求
         */
        private void sendBatchSmsRequest(String templateCode, String phoneJson, String paramJson, String signJson) {
            // 传入必要参数
            logger.debug("[SMSNoticeService] -> [传入必要参数][PhoneNumbers:{}][TemplateParam:{}]", phoneJson, paramJson);
            CommonRequest request = new CommonRequest();

            request.setSysMethod(MethodType.POST);
            request.setSysDomain(smsProperty.getSmsDomain());
            request.setSysVersion(smsProperty.getSmsVersion());
            request.setSysAction(smsProperty.getSmsSendBatchCode());

            request.putQueryParameter("TemplateCode", templateCode);
            request.putQueryParameter("PhoneNumberJson", phoneJson);
            request.putQueryParameter("TemplateParamJson", paramJson);
            request.putQueryParameter("SignNameJson", signJson);

            try {
                logger.debug("[SMSNoticeService] -> 发送短信通知");
                Date now = new Date();
                CommonResponse response = Client.getInstance().getCommonResponse(request);
                SMSSendResult result = FastJsonUtils.toBean(response.getData(), SMSSendResult.class);

                // 插入发送短信日志
                smsLogService.createSendLog(new SMSSendLog().toBuilder().sendApi(smsProperty.getSmsSendBatchCode())
                        .sendPhone(phoneJson).templateType(SMSTempType.SMS_NOTICE.name()).templateCode(templateCode)
                        .bizId(result.getBizId()).sendTime(now).requestCode(result.getCode())
                        .requestMessage(result.getMessage()).status(SMSSendState.WAIT.getCode()).statusDetail("-").build());

                // 发送短信结果判断
                if (!result.getCode().equals(AliSMSSendCode.OK.getCode())) {
                    logger.error("[SMSNoticeService] -> 发送短信失败，内容：{}", FastJsonUtils.toJSONFeatures(result));
                    throw new Exception(AliSMSSendCode.getValue(result.getCode()));
                }
                logger.debug("[SMSNoticeService] -> 发送短信成功");
            } catch (ServerException e) {
                logger.error("[SMSNoticeService] -> 发送手机短信服务端报错：{}", e.getMessage());
                throw new SuperBootException(e);
            } catch (ClientException e) {
                logger.error("[SMSNoticeService] -> 发送手机短信服务端报错：{}", e.getMessage());
                throw new SuperBootException(e);
            } catch (Exception e) {
                logger.error("[SMSNoticeService] -> 发送手机短信错误：{}", e.getMessage());
                throw new SuperBootException(e);
            }
        }

        /**
         * -------------------------------------sendSms-------------------------------------------
         **/

        /**
         * @Description: 发送短信、使用默认模板 只有三个参数content name count
         */
        private void sendSmsByDefault() {
            String signName = smsProperty.getSmsSignName();
            String templateCode = smsProperty.getSmsGiveCardTemplateCode();

            // 这里注意因为参数不同，所以循环发送短信
            for (SMSCommonVo param : paramList) {
                String phone = param.getPhone();
                JSONObject paramJsonObject = new JSONObject(3);
                try {
                    // 封装传入变量
                    Map<String, Object> paramMap = FastJsonUtils.stringToCollect(param.getParam());
                    paramJsonObject.put(smsProperty.getSmsParamContent(),
                            paramMap.get(smsProperty.getSmsParamContent()));
                    paramJsonObject.put(smsProperty.getSmsParamName(), paramMap.get(smsProperty.getSmsParamName()));
                    paramJsonObject.put(smsProperty.getSmsParamCount(), paramMap.get(smsProperty.getSmsParamCount()));
                } catch (Exception e) {
                    logger.error("[SMSNoticeService] -> 手机号为{}的用户获取发送短信参数异常,参数{}, 错误信息：{}", param.getPhone(),
                            param.getParam(), e.getMessage());
                    continue;
                }
                String paramJson = FastJsonUtils.toJSONFeatures(paramJsonObject);
                sendSmsRequest(templateCode, phone, paramJson, signName);
            }
        }

        /**
         * @Description: 发送短信、使用自定义模板
         */
        private void sendSms(Set<Object> paramNames, Map<String, Object> fixParams, String templateCode) {
            String signName = smsProperty.getSmsSignName();

            // 这里注意因为参数不同，所以循环发送短信
            for (SMSCommonVo param : paramList) {
                String phone = param.getPhone();
                JSONObject paramJsonObject = new JSONObject(3);
                try {
                    // 参数名列表
                    if (paramNames != null) {
                        // 封装传入变量
                        Map<String, Object> paramMap = FastJsonUtils.stringToCollect(param.getParam());
                        paramJsonObject = getSmsParamJsonObject(paramNames, fixParams, paramMap);
                    }
                } catch (Exception e) {
                    logger.error("[SMSNoticeService] -> 手机号为{}的用户获取发送短信参数异常,参数{}, 错误信息：{}", param.getPhone(),
                            param.getParam(), e.getMessage());
                    continue;
                }
                String paramJson = FastJsonUtils.toJSONFeatures(paramJsonObject);
                sendSmsRequest(templateCode, phone, paramJson, signName);
            }
        }

        /**
         * @param templateCode
         * @param phone
         * @param paramJson
         * @param signName
         * @Description: 将参数写入并请求
         */
        private void sendSmsRequest(String templateCode, String phone, String paramJson, String signName) {
            // 传入必要参数
            logger.debug("[SMSNoticeService] -> [传入必要参数][PhoneNumbers:{}][TemplateParam:{}]", phone, paramJson);
            CommonRequest request = new CommonRequest();

            request.setSysMethod(MethodType.POST);
            request.setSysDomain(smsProperty.getSmsDomain());
            request.setSysVersion(smsProperty.getSmsVersion());
            request.setSysAction(smsProperty.getSmsSendCode());

            request.putQueryParameter("PhoneNumbers", phone);
            request.putQueryParameter("TemplateCode", templateCode);
            request.putQueryParameter("TemplateParam", paramJson);
            request.putQueryParameter("SignName", signName);

            try {
                logger.debug("[SMSNoticeService] -> 发送短信通知");
                Date now = new Date();
                CommonResponse response = Client.getInstance().getCommonResponse(request);
                SMSSendResult result = FastJsonUtils.toBean(response.getData(), SMSSendResult.class);

                // 插入发送短信日志
                smsLogService.createSendLog(new SMSSendLog().toBuilder().sendApi(smsProperty.getSmsSendCode())
                        .sendPhone(phone).templateType(SMSTempType.SMS_NOTICE.name()).templateCode(templateCode)
                        .bizId(result.getBizId()).sendTime(now).requestCode(result.getCode())
                        .requestMessage(result.getMessage()).status(SMSSendState.WAIT.getCode()).statusDetail("-").build());

                // 发送短信结果判断
                if (!result.getCode().equals(AliSMSSendCode.OK.getCode())) {
                    logger.error("[SMSNoticeService] -> 发送短信失败，内容：{}", FastJsonUtils.toJSONFeatures(result));
                    throw new Exception(AliSMSSendCode.getValue(result.getCode()));
                }
                logger.debug("[SMSNoticeService] -> 发送短信成功");
            } catch (ServerException e) {
                logger.error("[SMSNoticeService] -> 发送手机短信服务端报错：{}", e.getMessage());
                throw new SuperBootException(e);
            } catch (ClientException e) {
                logger.error("[SMSNoticeService] -> 发送手机短信服务端报错：{}", e.getMessage());
                throw new SuperBootException(e);
            } catch (Exception e) {
                logger.error("[SMSNoticeService] -> 发送手机短信错误：{}", e.getMessage());
                throw new SuperBootException(e);
            }
        }

        /**
         * @param paramNames 传入变量名
         * @param fixParams  传入的固定参数
         * @param paramMap   传入变量值json
         * @return
         * @Description: 封装传入参数
         */
        private JSONObject getSmsParamJsonObject(Set<Object> paramNames, Map<String, Object> fixParams,
                                                 Map<String, Object> paramMap) {
            JSONObject paramJsonObject = new JSONObject(paramNames.size());
            // 写入参数
            for (Object object : paramNames) {
                String paramName = (String) object;
                if (fixParams != null && fixParams.containsKey(paramName)) {
                    paramJsonObject.put(paramName, fixParams.get(paramName));
                    continue;
                }
                if (paramMap != null && paramMap.containsKey(paramName)) {
                    paramJsonObject.put(paramName, paramMap.get(paramName));
                    continue;
                }
                logger.error("[SMSNoticeService] -> 存在传入变量{}没有传入值", paramName);
                throw new SuperBootException("exp-superboot-sms-0010", paramName);
            }
            return paramJsonObject;
        }

        /**
         * @return 存在返回true，不存在返回false
         * @Description: 判断参数列表是否存在重复的手机号
         */
        private boolean checkPhoneRepeat() {
            List<String> phoneList = paramList.stream().map(param -> param.getPhone()).collect(Collectors.toList());
            HashSet<String> set = new HashSet<String>(phoneList);
            if (phoneList.size() == set.size()) {
                return false;
            }
            logger.debug("[SMSNoticeService] -> 存在重复手机号，可能造成重复的手机号收到多条短信");
            return true;
        }

    }
}
