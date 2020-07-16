package com.lhq.superboot.wechat.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.lhq.superboot.domain.WxOrderLog;
import com.lhq.superboot.domain.WxOrderLogExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lhq.superboot.domain.WechatPayParam;
import com.lhq.superboot.enums.WxOrderState;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.mapper.WxOrderLogMapper;
import com.lhq.superboot.wechat.WechatOrderLog;

/**
 * @Description: 支付日志实现
 * @author: lihaoqi
 * @date: 2019年7月4日 下午3:29:53
 * @version: v1.0.0
 */
@Service
public class WechatOrderLogImpl implements WechatOrderLog {

    private static final Logger logger = LoggerFactory.getLogger(WechatOrderLog.class);

    @Autowired
    private WxOrderLogMapper wxOrderLogMapper;

    @Override
    public List<WxOrderLog> queryWxOrderLogListByOrderNo(String orderNo) {
        WxOrderLogExample example = new WxOrderLogExample();
        example.createCriteria().andOrderNoEqualTo(orderNo);
        return wxOrderLogMapper.selectByExample(example);
    }

    @Override
    @Transactional
    public void insertWxOrderLog(WxOrderState wxOrderState, WechatPayParam wxPayParam, String prepay_id, String sign,
                                 String errCode, String errReason) {
        logger.debug("[WechatOrderLog] -> 记录微信下单日志");
        try {
            if (wxOrderState == WxOrderState.ORDER_SUCCESS) {
                WxOrderLog wxOrderLog = new WxOrderLog().toBuilder().openId(wxPayParam.getOpenid())
                        .userId(wxPayParam.getUserId()).outTradeNo(wxPayParam.getOrderNo())
                        .orderState(wxOrderState.getCode()).prepayId(prepay_id).sign(sign)
                        .totalFee(Long.parseLong(wxPayParam.getMoney())).modifyTime(new Date()).build();
                wxOrderLogMapper.insertSelective(wxOrderLog);
            } else if (wxOrderState == WxOrderState.ORDER_FAIL) {
                WxOrderLog wxOrderLog = new WxOrderLog().toBuilder().openId(wxPayParam.getOpenid())
                        .userId(wxPayParam.getUserId()).outTradeNo(wxPayParam.getOrderNo())
                        .orderState(wxOrderState.getCode()).modifyTime(new Date())
                        .totalFee(Long.parseLong(wxPayParam.getMoney())).errCode(errCode).errReason(errReason).build();
                wxOrderLogMapper.insertSelective(wxOrderLog);
            }
        } catch (Exception e) {
            logger.error("[WechatOrderLog] -> 记录微信下单出错，错误：{}", e.getMessage());
        }

    }

    @Override
    public boolean checkTotalFee(String outTradeNo, String totalFee) {
        WxOrderLogExample example = new WxOrderLogExample();
        example.createCriteria().andOutTradeNoEqualTo(outTradeNo);

        List<WxOrderLog> wxOrderLogList = wxOrderLogMapper.selectByExample(example);
        if (wxOrderLogList.isEmpty()) {
            logger.error("[WechatOrderLog] -> 无法查询到订单，订单号：{}", outTradeNo);
            throw new SuperBootException("lhq-superboot-wechat-0002", outTradeNo);
        }
        if (wxOrderLogList.size() > 1) {
            logger.error("[WechatOrderLog] -> 存在重复订单号，订单号：{}", outTradeNo);
            throw new SuperBootException("lhq-superboot-wechat-0003", outTradeNo);
        }

        return totalFee.equals(wxOrderLogList.get(0).getTotalFee().toString());
    }

    @Override
    @Transactional
    public void updateWxOrderLog(WxOrderState wxOrderState, String transactionId, String outTradeNo, String payTime,
                                 String errCode, String errReason) {
        logger.debug("[WechatOrderLog] -> 更新微信下单日志状态");
        WxOrderLogExample wxOrderExample = new WxOrderLogExample();
        wxOrderExample.createCriteria().andOutTradeNoEqualTo(outTradeNo);

        try {
            if (WxOrderState.PAY_SUCCESS == wxOrderState) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                WxOrderLog wxOrderLog = new WxOrderLog().toBuilder().payTime(simpleDateFormat.parse(payTime))
                        .modifyTime(new Date()).orderState(wxOrderState.getCode()).transactionId(transactionId).build();
                wxOrderLogMapper.updateByExampleSelective(wxOrderLog, wxOrderExample);
            } else if (WxOrderState.ORDER_FAIL == wxOrderState || WxOrderState.PAY_FAIL == wxOrderState) {
                WxOrderLog wxOrderLog = new WxOrderLog().toBuilder().errCode(errCode).errReason(errReason)
                        .modifyTime(new Date()).orderState(wxOrderState.getCode()).transactionId(transactionId).build();
                wxOrderLogMapper.updateByExampleSelective(wxOrderLog, wxOrderExample);
            } else if (WxOrderState.REFUND_SUCCESS == wxOrderState) {
                WxOrderLog wxOrderLog = new WxOrderLog().toBuilder().modifyTime(new Date())
                        .orderState(wxOrderState.getCode()).transactionId(transactionId).build();
                wxOrderLogMapper.updateByExampleSelective(wxOrderLog, wxOrderExample);
            } else if (WxOrderState.REFUND_FAIL == wxOrderState) {
                WxOrderLog wxOrderLog = new WxOrderLog().toBuilder().errCode(errCode).errReason(errReason)
                        .modifyTime(new Date()).orderState(wxOrderState.getCode()).transactionId(transactionId).build();
                wxOrderLogMapper.updateByExampleSelective(wxOrderLog, wxOrderExample);
            } else {
                logger.error("[WechatOrderLog] -> 未知支付状态");
            }
        } catch (Exception e) {
            logger.error("[WechatOrderLog] -> 更新微信预下单出错，错误：{}", e.getMessage());
        }
    }

}
