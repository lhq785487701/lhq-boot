package com.lhq.superboot.service;

import java.util.List;

import com.lhq.superboot.domain.SMSSendDetailMsg;
import com.lhq.superboot.domain.SMSSendDetailDTO;

/**
 * @Description: 查询短信详情接口
 * @author: lihaoqi
 * @date: 2019年8月19日 下午5:26:01
 * @version: v1.0.0
 */
public interface SMSQueryService {

    /**
     * @param sendLogId 短信日志id
     * @return
     * @Description: 查询短信日志id下的所有短信详情
     */
    public List<SMSSendDetailDTO> querySMSDetail(Integer sendLogId);

    /**
     * @param date
     * @param bizId
     * @param phone
     * @return
     * @Description: 通过bizid phont date查询短信记录
     */
    public SMSSendDetailMsg querySMSDetail(String date, String bizId, String phone);

}
