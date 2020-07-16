package com.lhq.superboot.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lhq.superboot.domain.PayLog;
import com.lhq.superboot.domain.PayLogExample;
import com.lhq.superboot.enums.ConstEnumUtils.IS_VAILD;
import com.lhq.superboot.mapper.PayLogMapper;
import com.lhq.superboot.service.PayLogService;

/**
 * @Description 金额支付日志实现
 * @author: lihaoqi
 * @date: 2019年7月18日 上午11:32:55
 * @version: v1.0.0
 */
@Service
public class PayLogServiceImpl implements PayLogService {

    private static final Logger logger = LoggerFactory.getLogger(PayLogService.class);

    @Autowired
    private PayLogMapper payLogMapper;

    @Override
    @Transactional
    public void insertPayLog(String userId, String orderNo, String outTradeNo, String payType, String bankType, Long price,
                             String timeEnd) {
        logger.debug("[PayLogService] -> 记录用户支付日志");
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date payTime = simpleDateFormat.parse(timeEnd);

            PayLog PayLog = new PayLog().toBuilder().outTradeNo(outTradeNo).orderNo(orderNo).payPrice(price)
                    .payType(payType).payTime(payTime).userId(userId).bankType(bankType).build();
            payLogMapper.insertSelective(PayLog);
        } catch (Exception e) {
            logger.error("[PayLogService] -> 记录用户支付日志，错误：{}", e.getMessage());
        }
    }

    @Override
    public void updateRefundPayLog(String outTradeNo) {
        logger.debug("[PayLogService] -> 记录用户退款日志");
        PayLogExample payLogExample = new PayLogExample();
        payLogExample.createCriteria().andOutTradeNoEqualTo(outTradeNo);

        try {
            PayLog payLog = new PayLog().toBuilder().modifyTime(new Date()).isValid(IS_VAILD.NO.value()).build();
            payLogMapper.updateByExampleSelective(payLog, payLogExample);
        } catch (Exception e) {
            logger.error("[PayLogService] -> 记录用户退款日志，错误：{}", e.getMessage());
        }

    }

}
