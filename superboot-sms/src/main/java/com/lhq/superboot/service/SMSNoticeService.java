package com.lhq.superboot.service;

import java.util.List;

import com.lhq.superboot.domain.SMSCommonVo;
import com.lhq.superboot.domain.SMSTemplate;

/**
 * @Description: 短信通知相关接口
 * @author: lihaoqi
 * @date: 2019年7月29日 上午11:28:54
 * @version: v1.0.0
 */
public interface SMSNoticeService {

    /**
     * @param paramList
     * @Description: 发送短信通知，使用默认模板
     */
    public void sendSMSNotice(List<SMSCommonVo> paramList);


    /**
     * @param paramList
     * @param template
     * @param isDefault 是否使用默认模板
     * @Description: 发送短信通知
     */
    public void sendSMSNotice(List<SMSCommonVo> paramList, SMSTemplate template, boolean isDefault);

}
