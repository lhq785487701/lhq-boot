package com.lhq.superboot.service;

import java.util.List;

import com.lhq.superboot.domain.SMSCommonVo;

/**
 * @Description: 阿里云发送短信通用api(暂无使用)
 * @author: lihaoqi
 * @date: 2019年7月29日 上午10:29:09
 * @version: v1.0.0
 */
public interface CommonMsgApiService {

    /**
     * @param paramList  手机号及需要传送的参数
     * @param templateId 模板id
     * @Description: 通过短信模板，发送短信
     */
    public void commonSendSMS(List<SMSCommonVo> paramList, String templateId);

    /**
     * @param paramList 手机号及需要传送的参数
     * @param userId    用户id
     * @param SMSType   短信类型
     * @Description: 通过短信模板，发送短信， 需设置个人的默认模板
     */
    public void commonSendSMSByUser(List<SMSCommonVo> paramList, String userId, String SMSType);

    /**
     * @param paramList 手机号及需要传送的参数
     * @param companyId 公司id
     * @param SMSType   短信类型
     * @Description: 通过短信模板，发送短信， 需设置公司的默认模板
     */
    public void commonSendSMSByCompany(List<SMSCommonVo> paramList, String companyId, String SMSType);
}
