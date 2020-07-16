package com.lhq.superboot.service;

import java.util.List;

import com.github.pagehelper.Page;
import com.lhq.superboot.domain.SMSSendDetail;
import com.lhq.superboot.domain.SMSSendLog;

/**
 * @Description: 短信日志
 * @author: lihaoqi
 * @date: 2019年8月19日 下午10:03:01
 * @version: v1.0.0
 */
public interface SMSLogService {

    /**
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     * @Description: 查询发送短信日志
     */
    public Page<SMSSendLog> querySendLog(String userId, int pageNum, int pageSize);

    /**
     * @return
     * @Description: 查询所有正在发送的短信
     */
    public List<SMSSendLog> querySendLogIsWait();

    /**
     * @param sendLogId
     * @return
     * @Description: 通过id查询发送短信日志
     */
    public SMSSendLog querySendLogById(Integer sendLogId);

    /**
     * @param smsSendLog
     * @Description: 插入发送短信日志(主表)
     */
    public void createSendLog(SMSSendLog smsSendLog);

    /**
     * @param smsSendLog
     * @Description: 更新发送短信日志
     */
    public void updateSendLog(SMSSendLog smsSendLog);

    /**
     * @param sendLogId
     * @param pageNum
     * @param pageSize
     * @return
     * @Description: 通过sendLogId分页查询详情表
     */
    public Page<SMSSendDetail> querySendDetailByPage(Integer sendLogId, int pageNum, int pageSize);

    /**
     * @param sendLogId
     * @return
     * @Description: 通过sendLogId查询详情表对应sendLogId的数据
     */
    public List<SMSSendDetail> querySendDetailById(Integer sendLogId);

    /**
     * @param smsSendDetail
     * @Description: 插入发送短信详情(从表)
     */
    public void createSendDetail(SMSSendDetail smsSendDetail);

    /**
     * @param smsSendDetail
     * @Description: 更新发送短信详情
     */
    public void updateSendDetail(SMSSendDetail smsSendDetail);


}
