package com.lhq.superboot.scheduler.sms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lhq.superboot.domain.SMSSendDetail;
import com.lhq.superboot.domain.SMSSendDetailDTO;
import com.lhq.superboot.domain.SMSSendDetailMsg;
import com.lhq.superboot.domain.SMSSendLog;
import com.lhq.superboot.enums.AliSMSState;
import com.lhq.superboot.enums.SMSSendState;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.SMSLogService;
import com.lhq.superboot.service.SMSQueryService;

/**
 * @Description: 发送短信日志查询定时器
 * @author: lihaoqi
 * @date: 2019年8月20日 上午1:10:36
 * @version: v1.0.0
 */
@Service
public class SMSSendLogQueryTask {

    private static final Logger logger = LoggerFactory.getLogger(SMSSendLogQueryTask.class);

    @Autowired
    private SMSLogService smsLogService;

    @Autowired
    private SMSQueryService smsQueryService;

    /**
     * @Description: 查询短信发送结果以及状态
     */
    public void querySMSSendLogDetail() {
        // 查询所有短信日志状态为正在发送且请求成功的短信
        List<SMSSendLog> smsList = smsLogService.querySendLogIsWait();
        logger.debug("[SMSSendLogQueryTask] -> 短信日志状态为正在发送的数据：{}", smsList);
        for (SMSSendLog smsSendLog : smsList) {
            // 处理发送短信日志
            handleSMSLog(smsSendLog);
        }
    }

    /**
     * @param smsSendLog
     * @Description: 处理短信日志
     */
    private void handleSMSLog(SMSSendLog smsSendLog) {
        int sendLogId = smsSendLog.getSmsSendLogId();
        String bizId = smsSendLog.getBizId();
        // 先查询短信详情表中是否已经存在该日志的数据
        List<SMSSendDetail> detailList = smsLogService.querySendDetailById(sendLogId);
        // 如果存在数据说明已经插入过详情表，只是存在短信的发送状态是等待回执;不存在数据则为第一次查询
        if (detailList.isEmpty()) {
            writeSMSDetail(sendLogId);
        } else {
            updateSMSDetail(detailList, bizId, sendLogId);
        }
    }

    /**
     * @param sendLogId
     * @Description: 首次查询短信详情，写入短信详情
     */
    private void writeSMSDetail(Integer sendLogId) {
        List<SMSSendDetailDTO> detailList = smsQueryService.querySMSDetail(sendLogId);
        // 该字段判断短信中是否存在尚未处理的短信、是否存在错误，且记录成功失败等待的条数
        int successCount = 0, failCount = 0, waitCount = 0;
        int status = SMSSendState.SUCCESS.getCode();

        for (SMSSendDetailDTO detail : detailList) {
            // 插入短信详情
            try {
                // 接受时间存在为空的情况
                Date receiveDate = detail.getReceiveDate() == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(detail.getReceiveDate());
                Date sendDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(detail.getSendDate());

                smsLogService.createSendDetail(new SMSSendDetail().toBuilder()
                        .content(detail.getContent())
                        .errCode(detail.getErrCode())
                        .outId(detail.getOutId())
                        .phone(detail.getPhoneNum())
                        .receiveDate(receiveDate)
                        .sendDate(sendDate)
                        .sendStatus(detail.getSendStatus())
                        .smsSendLogId(sendLogId)
                        .templateCode(detail.getTemplateCode()).build());
            } catch (Exception e) {
                logger.debug("[SMSSendLogQueryTask] -> 插入短信详情异常", e.getMessage());
                continue;
            }

            // 短信状态处理
            int sendStatus = detail.getSendStatus();
            if (sendStatus == AliSMSState.WAIT.getCode()) {
                waitCount++;
            } else if (sendStatus == AliSMSState.ERROR.getCode()) {
                failCount++;
            } else {
                successCount++;
            }
        }

        if (waitCount > 0) {
            status = SMSSendState.WAIT.getCode();
        } else if (failCount > 0) {
            status = SMSSendState.ERROR.getCode();
        }

        // 更新短信日志的信息与状态
        SMSSendLog smsSendLog = new SMSSendLog().toBuilder().smsSendLogId(sendLogId).sendCount("" + detailList.size()).status(status)
                .statusDetail(successCount + "/" + failCount + "/" + waitCount).build();
        smsLogService.updateSendLog(smsSendLog);
    }

    /**
     * @param detailList
     * @param bizId
     * @param sendLogId
     * @Description: 更新日志与日志详情状态
     */
    private void updateSMSDetail(List<SMSSendDetail> detailList, String bizId, Integer sendLogId) {
        // 该字段判断短信中是否存在尚未处理的短信、是否存在错误
        int successCount = 0, failCount = 0, waitCount = 0;
        int status = SMSSendState.SUCCESS.getCode();

        for (SMSSendDetail detail : detailList) {
            // 找出状态为wait的记录，查询接口看是否已经处理，处理则更新
            if (detail.getSendStatus() == AliSMSState.WAIT.getCode()) {
                String date = new SimpleDateFormat("yyyyMMdd").format(detail.getSendDate());
                String phone = detail.getPhone();
                int sendDetailId = detail.getSmsSendDetailId();

                SMSSendDetailMsg smsDetailMsg = smsQueryService.querySMSDetail(date, bizId, phone);
                List<SMSSendDetailDTO> smsDetailList = smsDetailMsg.getSmsSendDetailDTOs().getSmsSendDetailDTO();

                SMSSendDetailDTO smsDetail = null;
                if (smsDetailList.size() == 1) {
                    smsDetail = smsDetailList.get(0);
                } else if (smsDetailList.size() > 1) {
                    // 存在多条时判断content
                    for (int i = 0; i < smsDetailList.size(); i++) {
                        SMSSendDetailDTO detail01 = smsDetailList.get(i);
                        if (detail01.getContent().equals(detail.getContent())) {
                            smsDetail = detail01;
                        }
                    }
                }
                if (smsDetail == null) {
                    throw new SuperBootException("lhq-superboot-sms-0023", date, bizId, phone);
                }

                int sendStatus = smsDetail.getSendStatus();
                // 处理状态不为等待回执时、需要更新日志详情
                if (sendStatus != AliSMSState.WAIT.getCode()) {
                    try {
                        // 接受时间存在为空的情况
                        Date receiveDate = smsDetail.getReceiveDate() == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(smsDetail.getReceiveDate());
                        smsLogService.updateSendDetail(new SMSSendDetail().toBuilder()
                                .smsSendDetailId(sendDetailId)
                                .errCode(smsDetail.getErrCode())
                                .sendStatus(sendStatus)
                                .receiveDate(receiveDate).build());
                    } catch (ParseException e) {
                        logger.debug("[SMSSendLogQueryTask] -> 更新短信详情异常", e.getMessage());
                        continue;
                    }
                }

                if (sendStatus == AliSMSState.ERROR.getCode()) {
                    failCount++;
                } else if (sendStatus == AliSMSState.WAIT.getCode()) {
                    waitCount++;
                } else {
                    successCount++;
                }
            } else if (detail.getSendStatus() == AliSMSState.ERROR.getCode()) {
                failCount++;
            } else {
                successCount++;
            }
        }

        if (waitCount > 0) {
            status = SMSSendState.WAIT.getCode();
        } else if (failCount > 0) {
            status = SMSSendState.ERROR.getCode();
        }

        // 更新短信日志的信息与状态、总量不需要更新
        SMSSendLog smsSendLog = new SMSSendLog().toBuilder().smsSendLogId(sendLogId).status(status)
                .statusDetail(successCount + "/" + failCount + "/" + waitCount).build();
        smsLogService.updateSendLog(smsSendLog);
    }

}
