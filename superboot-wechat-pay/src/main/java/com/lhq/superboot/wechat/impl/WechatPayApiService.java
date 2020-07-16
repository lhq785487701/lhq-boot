package com.lhq.superboot.wechat.impl;

import com.alibaba.fastjson.JSON;
import com.lhq.superboot.domain.*;
import com.lhq.superboot.enums.ConstEnumUtils.IS_HANDLE;
import com.lhq.superboot.enums.NotifyCode;
import com.lhq.superboot.enums.OrderCode;
import com.lhq.superboot.enums.SignType;
import com.lhq.superboot.enums.WxOrderState;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.property.WechatProperty;
import com.lhq.superboot.property.WxPayProperty;
import com.lhq.superboot.util.*;
import com.lhq.superboot.wechat.WechatOrderLog;
import com.lhq.superboot.wechat.WechatPayApi;
import com.lhq.superboot.wechat.util.WechatPayConstants;
import com.lhq.superboot.wechat.util.WechatPayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @Description: wechat支付接口
 * @author: lihaoqi
 * @date: 2019年6月27日 下午2:21:21
 * @version: v1.0.0
 */
@Service
public class WechatPayApiService implements WechatPayApi {

    private static final Logger logger = LoggerFactory.getLogger(WechatPayApi.class);

    /**
     * 成功失败状态码
     **/
    private static final String SUCCESS = "SUCCESS";
    private static final String FAIL = "FAIL";

    @Autowired
    private WechatProperty wechatProperty;

    @Autowired
    private WxPayProperty wxPayProperty;

    @Autowired
    private WechatOrderLog wechatOrderLog;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private HttpServletRequest request;

    @Override
    @Transactional
    public String unifiedOrder(WechatPayParam wxPayParam) {
        logger.info("[WechatPayApiService] -> 微信统一下单 start");
        if (StringUtils.isEmpty(wxPayParam.getOpenid()) || StringUtils.isEmpty(wxPayParam.getMoney()) || StringUtils.isEmpty(wxPayParam.getOrderNo())
                || StringUtils.isEmpty(wxPayParam.getOutTradeNo()) || StringUtils.isEmpty(wxPayParam.getUserId())
                || StringUtils.isEmpty(wxPayParam.getNotifyUrl()) || StringUtils.isEmpty(wxPayParam.getPayType())
                || StringUtils.isEmpty(wxPayParam.getBody())) {
            logger.info("[WechatPayApiService] -> 参数有误，请检查参数是否完整且必填：{}", wxPayParam.toString());
            throw new SuperBootException("lhq-superboot-wechat-0001");
        }
        // 组装预下单的请求数据
        String reqParam = getPayParam(wxPayParam);
        logger.debug("[WechatPayApiService] -> 获取下单传参 {}", reqParam);

        // 预下单post请求
        String result = HttpClientUtils.httpPostToString(wxPayProperty.getUnifiedOrderUrl(), reqParam);
        logger.debug("[WechatPayApiService] -> 下单结果：result:{}", result);
        Map<String, String> returnMap = null;
        try {
            returnMap = WechatPayUtils.xmlToMap(result);
        } catch (Exception e) {
            logger.error("xml转map出错， 错误信息：{}", e.getMessage());
            throw new SuperBootException("lhq-superboot-wechat-0009", e.getMessage());
        }

        String returnCode = returnMap.get("return_code");
        String returnMsg = returnMap.get("return_msg");
        if (!SUCCESS.equals(returnCode)) {
            wechatOrderLog.insertWxOrderLog(WxOrderState.ORDER_FAIL, wxPayParam, null, null, null, returnMsg);
            return null;
        }

        String resultCode = returnMap.get("result_code");
        String errCode = returnMap.get("err_code");
        String errCodeDes = returnMap.get("err_code_des");
        if (!SUCCESS.equals(resultCode)) {
            wechatOrderLog.insertWxOrderLog(WxOrderState.ORDER_FAIL, wxPayParam, null, null, errCode, errCodeDes);
            return null;
        }
        String prepay_id = returnMap.get("prepay_id");
        Long orderTime = new Date().getTime();

        if (prepay_id == null) {
            wechatOrderLog.insertWxOrderLog(WxOrderState.ORDER_FAIL, wxPayParam, null, null, null, "prepay_id为空");
            return null;
        }
        // 组装返回数据，并再次签名
        Map<String, String> map = getPayResult(prepay_id);
        // 记录下单日志
        wechatOrderLog.insertWxOrderLog(WxOrderState.ORDER_SUCCESS, wxPayParam, prepay_id, map.get("paySign"), null, null);
        // 将下单需要信息存放redis缓存
        WechatOrderCache wxOrder = new WechatOrderCache().toBuilder().money(wxPayParam.getMoney()).orderTime(orderTime)
                .orderNo(wxPayParam.getOrderNo()).outTradeNo(wxPayParam.getOutTradeNo()).payType(wxPayParam.getPayType())
                .userId(wxPayParam.getUserId()).isHandle(IS_HANDLE.NO.value()).build();
        setOrderMsg(wxPayParam.getOutTradeNo(), wxOrder);
        // 返回5个参数和sign
        return JSON.toJSONString(map);
    }

    @Override
    @Transactional
    public WechatPayNotifyDto wechatPayNotify() throws Exception {
        logger.info("[WechatPayApiService] -> 微信支付通知 start");
        // 将通知参数读取
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            logger.error("读取内容错误， 错误信息：{}", e.getMessage());
            throw new SuperBootException("lhq-superboot-wechat-0010", e.getMessage());
        } finally {
            if (br != null) {
                br.close();
            }
        }

        // sb为微信返回的xml
        String notityXml = sb.toString();
        // 解析并给微信发回收到通知确认
        Map<String, String> notityMap;
        try {
            notityMap = WechatPayUtils.xmlToMap(notityXml);
        } catch (Exception e) {
            logger.error("xml转map出错， 错误信息：{}", e.getMessage());
            throw new SuperBootException("lhq-superboot-wechat-0009", e.getMessage());
        }
        String returnCode = notityMap.get("return_code");
        String returnDesc = notityMap.get("return_msg");
        // 判断支付结果
        if (!SUCCESS.equals(returnCode)) {
            logger.error("[WechatPayApiService] -> 通知retrun_code为fail,原因：{}", returnDesc);
            return new WechatPayNotifyDto().toBuilder().result(WechatPayConstants.NOTIFY_FAIL).code(NotifyCode.PAY_FAIL.getCode()).desc(returnDesc)
                    .build();
        }

        String resultCode = notityMap.get("result_code");
        String totalFee = notityMap.get("total_fee");
        String outTradeNo = notityMap.get("out_trade_no");
        String payTime = notityMap.get("time_end");
        String errCode = notityMap.get("err_code");
        String errCodeDes = notityMap.get("err_code_des");
        String bankType = notityMap.get("bank_type");
        String transactionId = notityMap.get("transaction_id");
        logger.debug("[WechatPayApiService] -> 获取微信通知返回信息：{}", notityMap);

        // 获取缓存中的订单信息
        WechatOrderCache wxOrder = getOrderMsg(outTradeNo);
        String money = wxOrder.getMoney();
        Integer isHandle = wxOrder.getIsHandle();

        // 已处理返回、不需重复处理
        if (IS_HANDLE.NO.value() != isHandle) {
            logger.error("[WechatPayApiService] -> 该订单已经处理完毕：{}", outTradeNo);
            return new WechatPayNotifyDto().toBuilder().result(WechatPayConstants.SUCCESS).code(NotifyCode.HANDLED.getCode())
                    .desc(NotifyCode.HANDLED.getValue()).build();
        }

        // 判断签名是否一致
        if (!WechatPayUtils.isSignatureValid(notityXml, wechatProperty.getApiKey())) {
            return new WechatPayNotifyDto().toBuilder().result(WechatPayConstants.SIGN_FAIL).code(NotifyCode.SIGN_FAIL.getCode())
                    .desc(NotifyCode.SIGN_FAIL.getValue()).build();
        }

        // 金额校验
        if (!money.equals(totalFee)) {
            return new WechatPayNotifyDto().toBuilder().result(WechatPayConstants.MONEY_FAIL).code(NotifyCode.PAY_MONEY_FAIL.getCode())
                    .desc(NotifyCode.PAY_MONEY_FAIL.getValue()).build();
        }

        // 金额与签名校验成功后，将订单状态设置为处理中
        wxOrder.setIsHandle(IS_HANDLE.HANDLING.value());
        setOrderMsg(outTradeNo, wxOrder);

        // resultCode是支付成功失败的标识
        WechatPayNotifyDto.WechatPayNotifyDtoBuilder wxPayNotifyBuild = new WechatPayNotifyDto().toBuilder();
        if (!resultCode.equals(SUCCESS)) {
            // 支付失败、返回通知成功的信息(意为已经接受成功)
            wechatOrderLog.updateWxOrderLog(WxOrderState.PAY_FAIL, transactionId, outTradeNo, payTime, errCode, errCodeDes);
            wxPayNotifyBuild.result(WechatPayConstants.SUCCESS).code(NotifyCode.PAY_FAIL.getCode()).desc(NotifyCode.PAY_FAIL.getValue());
        } else {
            // 支付成功
            // 更新微信下单日志信息
            wechatOrderLog.updateWxOrderLog(WxOrderState.PAY_SUCCESS, transactionId, outTradeNo, payTime, errCode, errCodeDes);
            wxPayNotifyBuild.result(WechatPayConstants.SUCCESS).code(NotifyCode.SUCCESS.getCode()).bankType(bankType)
                    .desc(NotifyCode.SUCCESS.getValue()).outTradeNo(outTradeNo).money(Long.parseLong(totalFee)).payTime(payTime);
        }
        // 记录订单状态、返回支付通知的信息、设置以及处理
        wxOrder.setIsHandle(IS_HANDLE.YES.value());
        WechatPayNotifyDto wxPayNotifyDto = wxPayNotifyBuild.build();
        setOrderMsg(outTradeNo, wxOrder);
        return wxPayNotifyDto;
    }

    @Override
    @Transactional
    public WechatRefundDto wechatRefund(WechatRefundParam wxRefundParam) {
        logger.info("[WechatPayApiService] -> 微信退款 start");
        if (StringUtils.isEmpty(wxRefundParam.getOrderNo()) || StringUtils.isEmpty(wxRefundParam.getRefundNo())
                || StringUtils.isEmpty(wxRefundParam.getMoney())) {
            logger.info("[WechatPayApiService] -> 参数有误，请检查参数是否完整且必填：{}", wxRefundParam.toString());
            throw new SuperBootException("lhq-superboot-wechat-0004");
        }
        // 组装预下单的请求数据
        String reqParam = getRefundParam(wxRefundParam);
        logger.debug("[WechatPayApiService] -> 获取退款传参 {}", reqParam);

        // 退款post请求
        String result = HttpClientUtils.httpPostToString(wxPayProperty.getRefundUrl(), reqParam);
        logger.debug("[WechatPayApiService] -> 退款结果：result:{}", result);
        Map<String, String> returnMap = null;
        try {
            returnMap = WechatPayUtils.xmlToMap(result);
        } catch (Exception e) {
            logger.error("xml转map出错， 错误信息：{}", e.getMessage());
            throw new SuperBootException("lhq-superboot-wechat-0009", e.getMessage());
        }

        String returnCode = returnMap.get("return_code");
        String returnMsg = returnMap.get("return_msg");
        if (!SUCCESS.equals(returnCode)) {
            return new WechatRefundDto().toBuilder().refundResult(FAIL).resultDesc(returnMsg).resultMap(returnMap).build();
        }
        String resultCode = returnMap.get("result_code");
        String errCode = returnMap.get("err_code");
        String errCodeDes = returnMap.get("err_code_des");
        String transactionId = returnMap.get("transaction_id");
        if (!SUCCESS.equals(resultCode)) {
            wechatOrderLog.updateWxOrderLog(WxOrderState.REFUND_FAIL, transactionId, wxRefundParam.getRefundNo(), null, errCode, errCodeDes);
            return new WechatRefundDto().toBuilder().refundResult(FAIL).resultDesc(errCodeDes).resultMap(returnMap).build();
        }

        // 更新微信订单日志
        wechatOrderLog.updateWxOrderLog(WxOrderState.REFUND_SUCCESS, transactionId, wxRefundParam.getRefundNo(), null, null, null);

        return new WechatRefundDto().toBuilder().refundResult(SUCCESS).resultDesc(errCodeDes).resultMap(returnMap).build();
    }

    @Override
    public WechatQueryOrderDto queryOrder(String outTradeNo) throws Exception {
        logger.info("[WechatPayApiService] -> 微信查询订单 start");

        // 获取查询订单必要参数
        String reqParam = getQueryOrderParam(outTradeNo);
        logger.debug("[WechatPayApiService] -> 获取传送参数 {}", reqParam);

        // 查询订单post请求
        String result = HttpClientUtils.httpPostToString(wxPayProperty.getQueryOrderUrl(), reqParam);
        logger.debug("[WechatPayApiService] -> 查询订单结果：result:{}", result);
        Map<String, String> returnMap = null;
        try {
            returnMap = WechatPayUtils.xmlToMap(result);
        } catch (Exception e) {
            logger.error("xml转map出错， 错误信息：{}", e.getMessage());
            throw new SuperBootException(e);
        }

        String returnCode = returnMap.get("return_code");
        String returnMsg = returnMap.get("return_msg");
        if (!SUCCESS.equals(returnCode)) {
            return new WechatQueryOrderDto().toBuilder().code(OrderCode.PARAM_ERROR.getCode()).desc(returnMsg).result(returnMap).build();
        }

        String resultCode = returnMap.get("result_code");
        String errCodeDes = returnMap.get("err_code_des");
        if (!SUCCESS.equals(resultCode)) {
            return new WechatQueryOrderDto().toBuilder().code(OrderCode.PARAM_ERROR.getCode()).desc(errCodeDes).result(returnMap).build();
        }

        // 判断签名是否一致
        if (!WechatPayUtils.isSignatureValid(result, wechatProperty.getApiKey())) {
            return new WechatQueryOrderDto().toBuilder().code(OrderCode.SIGN_ERROR.getCode()).desc(OrderCode.SIGN_ERROR.getValue()).result(returnMap)
                    .build();
        }

        // 匹配订单号
        String wxOutTradeNo = returnMap.get("out_trade_no");
        if (!wxOutTradeNo.equals(outTradeNo)) {
            return new WechatQueryOrderDto().toBuilder().code(OrderCode.ORDER_NOT_EQUAL.getCode()).desc(OrderCode.ORDER_NOT_EQUAL.getValue())
                    .result(returnMap).build();
        }

        // 交易状态
        String tradeState = returnMap.get("trade_state");
        String tradeStateDesc = returnMap.get("trade_state_desc");
        if (SUCCESS.equals(tradeState)) {
            return new WechatQueryOrderDto().toBuilder().code(OrderCode.SUCCESS.getCode()).desc(tradeStateDesc).result(returnMap).build();
        }

        return new WechatQueryOrderDto().toBuilder().code(OrderCode.TRADE_FAIL.getCode()).desc(tradeStateDesc).result(returnMap).build();
    }

    @Override
    @Transactional
    public WechatUnhandleDto dealUnHandleOrder(String outTradeNo, String userId) throws Exception {
        logger.info("[WechatPayApiService] -> 处理未处理的且超时的订单");

        // 查询订单信息
        WechatQueryOrderDto wxOrderDto = queryOrder(outTradeNo);
        int code = wxOrderDto.getCode();
        String desc = wxOrderDto.getDesc();
        Map<String, String> orderMap = wxOrderDto.getResult();
        // 交易状态code
        String tradeState = orderMap.get("trade_state");
        String transactionId = orderMap.get("transaction_id");

        // 交易状态
        if (code == OrderCode.SUCCESS.getCode()) {
            // 交易成功(支付成功)
            logger.debug("[WechatPayApiService] -> 查询订单号成功、支付成功");
            String payTime = orderMap.get("time_end");
            String bankType = orderMap.get("bank_type");
            String cashFee = orderMap.get("cash_fee");
            // 修改订单记录
            wechatOrderLog.updateWxOrderLog(WxOrderState.PAY_SUCCESS, transactionId, outTradeNo, payTime, tradeState, desc);
            return new WechatUnhandleDto().toBuilder().isSuccess(true).payTime(payTime).cashFee(cashFee).bankType(bankType).build();
        } else if (code == OrderCode.TRADE_FAIL.getCode()) {
            // 交易失败(支付失败)
            logger.debug("[WechatPayApiService] -> 查询订单号成功、支付失败，失败原因：{}", desc);
            // 修改订单记录
            wechatOrderLog.updateWxOrderLog(WxOrderState.PAY_FAIL, transactionId, outTradeNo, null, tradeState, desc);
            return new WechatUnhandleDto().toBuilder().isSuccess(false).build();
        } else {
            // 查询失败
            logger.error("[WechatPayApiService] -> 查询订单号失败，失败原因：{}", desc);
            throw new Exception(desc);
        }
    }

    @Override
    public List<WechatQueryOrderDto> queryOrderListByOrderNo(String orderNo) {
        if (StringUtils.isEmpty(orderNo)) {
            throw new SuperBootException("exp-haagen-wechat-0011");
        }
        // 从订单日志中获取所有的微信支付日志
        List<WxOrderLog> orderLogList = wechatOrderLog.queryWxOrderLogListByOrderNo(orderNo);
        if (!orderLogList.isEmpty()) {
            List<WechatQueryOrderDto> orderDtoList = new ArrayList<>();
            for (WxOrderLog orderLog : orderLogList) {
                try {
                    orderDtoList.add(queryOrder(orderLog.getOutTradeNo()));
                } catch (Exception e) {
                    logger.error("[WechatPayApiService] -> 查询订单号失败，失败原因：{}", e);
                }
            }
            return orderDtoList;
        } else {
            return null;
        }
    }

    @Override
    public void setOrderHandled(String outTradeNo, String orderNo) {
        WechatOrderCache wxOrder = getOrderMsg(outTradeNo);
        Integer isHandle = wxOrder.getIsHandle();
        if (!isHandle.equals(IS_HANDLE.NO.value())) {
            logger.error("[WechatPayApiService] -> 订单已处理，订单号：{}， 支付订单号：{}", orderNo, outTradeNo);
            return;
        }
        // 修改订单记录
        wechatOrderLog.updateWxOrderLog(WxOrderState.AUTO_CANCEL, null, outTradeNo, null, null, null);
        wxOrder.setIsHandle(IS_HANDLE.YES.value());
        setOrderMsg(outTradeNo, wxOrder);
    }

    /**
     * @param outTradeNo
     * @return
     * @Description: 获取缓存中的支付订单信息
     */
    private WechatOrderCache getOrderMsg(String outTradeNo) {
        Object orderCacheStr = redisUtils.get(wxPayProperty.getWechatOrderKey() + outTradeNo);
        if (orderCacheStr == null) {
            logger.error("[WechatPayApiService] -> 无法获取缓存中的订单信息，交易订单号：{}", outTradeNo);
            throw new SuperBootException("exp-haagen-wechat-0005", outTradeNo);
        }
        WechatOrderCache wxOrder;
        try {
            wxOrder = FastJsonUtils.toBean((String) orderCacheStr, WechatOrderCache.class);
        } catch (Exception e) {
            logger.error("[WechatPayApiService] -> 缓存订单信息转化出错， 交易订单号：{}", outTradeNo);
            throw new SuperBootException("exp-haagen-wechat-0006", outTradeNo);
        }
        return wxOrder;
    }

    /**
     * @param outTradeNo
     * @param wxOrder
     * @Description: 设置缓存中的支付订单信息
     */
    private void setOrderMsg(String outTradeNo, WechatOrderCache wxOrder) {
        redisUtils.set(wxPayProperty.getWechatOrderKey() + outTradeNo, FastJsonUtils.toJSONFeatures(wxOrder),
                wxPayProperty.getWechatOrderExpire());
    }

    /**
     * @param wxPayParam
     * @return
     * @Description: 组装预下单请求参数、获取支付参数
     */
    private String getPayParam(WechatPayParam wxPayParam) {
        logger.debug("[WechatPayApiService] -> 组装预下单请求参数，wxPayParam={}", wxPayParam);
        Map<String, String> data = new HashMap<>(20);
        data.put("appid", wechatProperty.getAppid());
        data.put("mch_id", wechatProperty.getMchid());
        data.put("nonce_str", WechatPayUtils.generateNonceStr());
        data.put("sign_type", wechatProperty.getSignType());
        data.put("body", wxPayParam.getBody());
        data.put("out_trade_no", wxPayParam.getOutTradeNo());
        data.put("fee_type", wxPayProperty.getFeeType());
        data.put("total_fee", wxPayParam.getMoney());
        data.put("spbill_create_ip", IPUtils.getIpAddr(request));
        data.put("notify_url", wxPayParam.getNotifyUrl());
        data.put("trade_type", wxPayProperty.getTradeType());
        data.put("openid", wxPayParam.getOpenid());
        try {
            String sign = WechatPayUtils.generateSignature(data, wechatProperty.getApiKey(), SignType.MD5);
            data.put("sign", sign);
        } catch (Exception e) {
            logger.error("[WechatPayApiService] -> 签名出错");
            throw new SuperBootException(e);
        }
        String reqParam;
        try {
            reqParam = WechatPayUtils.mapToXml(data);
        } catch (Exception e) {
            logger.error("[WechatPayApiService] -> map转xml出错");
            throw new SuperBootException(e);
        }
        return reqParam;
    }

    /**
     * @param prepayid
     * @return
     * @Description: 组装下单后结果返回客户端的请求数据、获取支付结果
     */
    private Map<String, String> getPayResult(String prepayid) {
        logger.debug("[WechatPayApiService] -> 根据当前的prepayid构造返回参数，prepayid={} ", prepayid);
        Map<String, String> map = new HashMap<>(10);
        map.put("appId", wechatProperty.getAppid());
        map.put("package", "prepay_id=" + prepayid);
        map.put("signType", wechatProperty.getSignType());
        map.put("nonceStr", WechatPayUtils.generateNonceStr());
        map.put("timeStamp", String.valueOf(WechatPayUtils.getCurrentTimestamp()));
        try {
            // 再次签名
            String sign = WechatPayUtils.generateSignature(map, wechatProperty.getApiKey(), SignType.MD5);
            map.put("paySign", sign);
            logger.info("[WechatPayApiService] -> 再次签名：{} ", sign);
        } catch (Exception e) {
            logger.error("[WechatPayApiService] -> 签名出错");
            throw new SuperBootException(e);
        }
        return map;
    }

    /**
     * @param wxRefundParam
     * @return
     * @Description: 组装退款参数
     */
    private String getRefundParam(WechatRefundParam wxRefundParam) {
        logger.debug("[WechatPayApiService] -> 组装退款请求参数，wxRefundParam={}", wxRefundParam);
        Map<String, String> data = new HashMap<>(20);
        data.put("appid", wechatProperty.getAppid());
        data.put("mch_id", wechatProperty.getMchid());
        data.put("nonce_str", WechatPayUtils.generateNonceStr());
        data.put("sign_type", wechatProperty.getSignType());
        data.put("out_trade_no", wxRefundParam.getOutTradeNo());
        data.put("out_refund_no", wxRefundParam.getRefundNo());
        data.put("fee_type", wxPayProperty.getFeeType());
        // 暂时只支持全退
        data.put("total_fee", wxRefundParam.getMoney());
        data.put("refund_fee", wxRefundParam.getMoney());
        data.put("trade_type", wxPayProperty.getTradeType());
        if (StringUtils.isNotEmpty(wxRefundParam.getRefundDesc())) {
            data.put("refund_desc", wxRefundParam.getRefundDesc());
        }
        if (StringUtils.isNotEmpty(wxRefundParam.getNotifyUrl())) {
            data.put("notify_url", wxRefundParam.getNotifyUrl());
        }
        try {
            String sign = WechatPayUtils.generateSignature(data, wechatProperty.getApiKey(), SignType.MD5);
            data.put("sign", sign);
        } catch (Exception e) {
            logger.error("[WechatPayApiService] -> 签名出错");
            throw new SuperBootException(e);
        }
        String reqParam;
        try {
            reqParam = WechatPayUtils.mapToXml(data);
        } catch (Exception e) {
            logger.error("[WechatPayApiService] -> map转xml出错");
            throw new SuperBootException(e);
        }
        return reqParam;
    }

    /**
     * @param outTradeNo
     * @return
     * @Description: 组装查询订单的参数
     */
    private String getQueryOrderParam(String outTradeNo) {
        logger.debug("[WechatPayApiService] -> 组装查询订单参数，交易订单号={}", outTradeNo);
        Map<String, String> data = new HashMap<>(10);
        data.put("appid", wechatProperty.getAppid());
        data.put("mch_id", wechatProperty.getMchid());
        data.put("out_trade_no", outTradeNo);
        data.put("sign_type", wechatProperty.getSignType());
        data.put("nonce_str", WechatPayUtils.generateNonceStr());
        try {
            // 再次签名
            String sign = WechatPayUtils.generateSignature(data, wechatProperty.getApiKey(), SignType.MD5);
            data.put("sign", sign);
            logger.info("[WechatPayApiService] -> 组装查询订单的参数签名：{} ", sign);
        } catch (Exception e) {
            logger.error("[WechatPayApiService] -> 签名出错");
            throw new SuperBootException(e);
        }
        String reqParam;
        try {
            reqParam = WechatPayUtils.mapToXml(data);
        } catch (Exception e) {
            logger.error("[WechatPayApiService] -> map转xml出错");
            throw new SuperBootException(e);
        }
        return reqParam;

    }

}
