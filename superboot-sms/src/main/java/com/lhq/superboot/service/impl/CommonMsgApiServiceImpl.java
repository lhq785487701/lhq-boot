package com.lhq.superboot.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lhq.superboot.domain.SMSCommonVo;
import com.lhq.superboot.domain.SMSTemplate;
import com.lhq.superboot.domain.UserSMSTemplate;
import com.lhq.superboot.domain.UserSMSTemplateExample;
import com.lhq.superboot.enums.SMSTempState;
import com.lhq.superboot.enums.SMSTempType;
import com.lhq.superboot.enums.ConstEnumUtils.IS_DEFAULT;
import com.lhq.superboot.enums.ConstEnumUtils.IS_DELETE;
import com.lhq.superboot.enums.ConstEnumUtils.IS_ENABLED;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.mapper.SMSTemplateMapper;
import com.lhq.superboot.mapper.UserSMSTemplateMapper;
import com.lhq.superboot.service.CommonMsgApiService;
import com.lhq.superboot.service.SMSCodeService;
import com.lhq.superboot.service.SMSNoticeService;
import com.lhq.superboot.service.SMSSpreadService;
import com.lhq.superboot.util.StringUtils;

/**
 * @Description: 通用短信接口实现类
 * @author: lihaoqi
 * @date: 2019年7月29日 上午10:30:58
 * @version: v1.0.0
 */
@Service
public class CommonMsgApiServiceImpl implements CommonMsgApiService {

    private static final Logger logger = LoggerFactory.getLogger(CommonMsgApiService.class);

    @Autowired
    private SMSCodeService smsCodeService;

    @Autowired
    private SMSNoticeService smsNoticeService;

    @Autowired
    private SMSSpreadService smsSpreadService;

    @Autowired
    private SMSTemplateMapper smsTempMapper;

    @Autowired
    private UserSMSTemplateMapper userTempMapper;

    @Override
    public void commonSendSMS(List<SMSCommonVo> paramList, String templateId) {
        if (StringUtils.isEmpty(templateId)) {
            logger.error("[CommonMsgApiService] -> 短信模板不能为空");
            throw new SuperBootException("lhq-superboot-sms-0001");
        }
        if (paramList.isEmpty()) {
            logger.error("[CommonMsgApiService] -> 暂无需要发送短信的内容");
            throw new SuperBootException("lhq-superboot-sms-0002");
        }
        SMSTemplate template = smsTempMapper.selectByPrimaryKey(templateId);
        if (template == null) {
            logger.error("[CommonMsgApiService] -> id为{}的模板不存在", templateId);
            throw new SuperBootException("lhq-superboot-sms-0003", templateId);
        }
        if (template.getIsDeleted() == IS_DELETE.YES.value()) {
            logger.error("[CommonMsgApiService] -> 短信模板已删除：{}", templateId);
            throw new SuperBootException("lhq-superboot-sms-0005", templateId);
        }
        if (template.getIsEnabled() == IS_ENABLED.NO.value()) {
            logger.error("[CommonMsgApiService] -> 短信模板已禁用：{}", templateId);
            throw new SuperBootException("lhq-superboot-sms-0006", templateId);
        }
        if (template.getTemplateState().equals(SMSTempState.AUDIT_PASS.getCode())) {
            logger.error("[CommonMsgApiService] -> 短信模板暂未通过审核：{}", templateId);
            throw new SuperBootException("lhq-superboot-sms-0007", templateId);
        }

        /** 根据短信模板类型发送短信 **/
        String smsType = template.getTemplateType();
        if (SMSTempType.SMS_CODE.getCode().equals(smsType)) {
            logger.debug("[CommonMsgApiService] -> 发送验证码类型的短信");
            // 验证码目前只支持单一手机发送,暂不建议使用公共发送短信发送验证码
            if (paramList.size() != 1) {
                logger.error("[CommonMsgApiService] -> 验证码只支持逐条发送");
                throw new SuperBootException("lhq-superboot-sms-0008");
            }
            smsCodeService.getSMSCode(paramList.get(0).getPhone(), template);

        } else if (SMSTempType.SMS_NOTICE.getCode().equals(smsType)) {
            logger.debug("[CommonMsgApiService] -> 发送短信通知类型的短信");
            smsNoticeService.sendSMSNotice(paramList, template, false);

        } else if (SMSTempType.SMS_SPREAD.getCode().equals(smsType)) {
            logger.debug("[CommonMsgApiService] -> 发送短信推广类型的短信");
            List<String> phoneList = paramList.stream().map(param -> param.getPhone()).collect(Collectors.toList());
            String templateCode = template.getTemplateCode();
            smsSpreadService.sendSMSSpread(phoneList, templateCode);

        } else {
            logger.error("[CommonMsgApiService] -> 短信模板类型：{} 不存在", smsType);
            throw new SuperBootException("lhq-superboot-sms-0004", smsType);
        }

    }

    @Override
    public void commonSendSMSByUser(List<SMSCommonVo> paramList, String userId, String SMSType) {
        // 通过短信类型与用户id查询用户设置的默认模板
        UserSMSTemplateExample example = new UserSMSTemplateExample();
        UserSMSTemplateExample.Criteria criteria = example.createCriteria();

        criteria.andUserIdEqualTo(userId).andTemplateTypeEqualTo(SMSType).andIsDefaultEqualTo(IS_DEFAULT.YES.value()).andIsEnabledEqualTo(IS_ENABLED.YES.value());
        example.setOrderByClause("create_time desc");
        List<UserSMSTemplate> smsTempList = userTempMapper.selectByExample(example);

        String templateId = null;
        if (smsTempList.isEmpty()) {
            logger.error("[CommonMsgApiService] -> 用户没有设置默认模板：{} ", userId);
            throw new SuperBootException("lhq-superboot-sms-0011", userId);
        }
        // 即使存在多个，取最新一个设置的默认
        templateId = smsTempList.get(0).getTemplateId();
        commonSendSMS(paramList, templateId);
    }

    @Override
    public void commonSendSMSByCompany(List<SMSCommonVo> paramList, String companyId, String SMSType) {
        // 通过短信类型与公司id查询用户设置的默认模板
        UserSMSTemplateExample example = new UserSMSTemplateExample();
        UserSMSTemplateExample.Criteria criteria = example.createCriteria();

        criteria.andCompanyIdEqualTo(companyId).andTemplateTypeEqualTo(SMSType).andIsDefaultEqualTo(IS_DEFAULT.YES.value()).andIsEnabledEqualTo(IS_ENABLED.YES.value());
        example.setOrderByClause("create_time desc");
        List<UserSMSTemplate> smsTempList = userTempMapper.selectByExample(example);

        String templateId = null;
        if (smsTempList.isEmpty()) {
            logger.error("[CommonMsgApiService] -> 该公司没有设置默认模板：{} ", companyId);
            throw new SuperBootException("lhq-superboot-sms-0012", companyId);
        }
        // 即使存在多个，取最新一个设置的默认
        templateId = smsTempList.get(0).getTemplateId();
        commonSendSMS(paramList, templateId);
    }

}
