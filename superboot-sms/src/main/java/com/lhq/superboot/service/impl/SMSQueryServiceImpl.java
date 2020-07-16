package com.lhq.superboot.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.lhq.superboot.client.Client;
import com.lhq.superboot.domain.SMSSendDetailDTO;
import com.lhq.superboot.domain.SMSSendDetailMsg;
import com.lhq.superboot.domain.SMSSendLog;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.property.SMSProperty;
import com.lhq.superboot.service.SMSLogService;
import com.lhq.superboot.service.SMSQueryService;
import com.lhq.superboot.util.FastJsonUtils;

/**
 * @Description: 查询短信详情实现类
 * @author: lihaoqi
 * @date: 2019年8月19日 下午5:25:46
 * @version: v1.0.0
 */
@Service
public class SMSQueryServiceImpl implements SMSQueryService {

    private static final Logger logger = LoggerFactory.getLogger(SMSQueryService.class);

    /**
     * 查询短信分页信息,正常情况下只有一条
     **/
    private static final String PAGE_SIZE = "50";
    private static final String CURRENT_PAGE = "1";

    @Autowired
    private SMSProperty smsProperty;

    @Autowired
    private SMSLogService smsLogService;

    @Override
    public List<SMSSendDetailDTO> querySMSDetail(Integer sendLogId) {
        logger.debug("[SMSDetailQuery] -> 查询短信详情，sendLogId = {}", sendLogId);
        List<SMSSendDetailDTO> detailList = new ArrayList<SMSSendDetailDTO>();

        // 获取发送短信日志的信息
        SMSSendLog sendLog = smsLogService.querySendLogById(sendLogId);

        String date = new SimpleDateFormat("yyyyMMdd").format(sendLog.getSendTime());
        String bizId = sendLog.getBizId();
        String api = sendLog.getSendApi();
        String phoneStr = sendLog.getSendPhone();

        // 判断发送短信的接口，SendBatchSms为记录手机json、SendSms为记录手机号
        SMSSendDetailMsg smsDetail = null;
        if (api.equals(smsProperty.getSmsSendCode())) {
            smsDetail = querySMSDetail(date, bizId, phoneStr);
            detailList.addAll(smsDetail.getSmsSendDetailDTOs().getSmsSendDetailDTO());
        } else if (api.equals(smsProperty.getSmsSendBatchCode())) {
            List<String> phoneList = FastJsonUtils.toList(phoneStr, String.class);
            Set<String> phoneSet = new HashSet<String>(phoneList);
            for (String phone : phoneSet) {
                smsDetail = querySMSDetail(date, bizId, phone);
                detailList.addAll(smsDetail.getSmsSendDetailDTOs().getSmsSendDetailDTO());
            }
        } else {
            logger.debug("[SMSDetailQuery] -> 不存在查询接口：{}，无法获取手机号", api);
            return null;
        }
        if (detailList.isEmpty()) {
            return null;
        }
        return detailList;
    }

    @Override
    public SMSSendDetailMsg querySMSDetail(String date, String bizId, String phone) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(smsProperty.getSmsDomain());
        request.setSysVersion(smsProperty.getSmsVersion());
        request.setSysAction(smsProperty.getSmsQueryCode());

        request.putQueryParameter("PhoneNumber", phone);
        request.putQueryParameter("PageSize", PAGE_SIZE);
        request.putQueryParameter("CurrentPage", CURRENT_PAGE);
        request.putQueryParameter("SendDate", date);
        request.putQueryParameter("BizId", bizId);

        try {
            CommonResponse response = Client.getInstance().getCommonResponse(request);
            SMSSendDetailMsg result = FastJsonUtils.toBean(response.getData(), SMSSendDetailMsg.class);
            logger.debug("[SMSDetailQuery] -> 查询短信详情: {}", result);
            return result;
        } catch (ServerException e) {
            logger.error("[SMSDetailQuery] -> 发送手机短信服务端报错：{}", e.getMessage());
            throw new SuperBootException(e);
        } catch (ClientException e) {
            logger.error("[SMSDetailQuery] -> 发送手机短信服务端报错：{}", e.getMessage());
            throw new SuperBootException(e);
        } catch (Exception e) {
            logger.error("[SMSDetailQuery] -> 发送手机短信错误：{}", e.getMessage());
            throw new SuperBootException(e);
        }
    }

}
