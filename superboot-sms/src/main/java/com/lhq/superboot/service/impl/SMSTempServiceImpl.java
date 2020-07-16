package com.lhq.superboot.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lhq.superboot.domain.SMSTemplate;
import com.lhq.superboot.domain.SMSTemplateExample;
import com.lhq.superboot.domain.UserManager;
import com.lhq.superboot.domain.UserSMSTemplate;
import com.lhq.superboot.domain.UserSMSTemplateExample;
import com.lhq.superboot.enums.SMSTempState;
import com.lhq.superboot.enums.ConstEnumUtils.IS_DELETE;
import com.lhq.superboot.enums.ConstEnumUtils.IS_ENABLED;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.mapper.SMSTemplateMapper;
import com.lhq.superboot.mapper.UserSMSTemplateMapper;
import com.lhq.superboot.repository.SMSTemplateRepository;
import com.lhq.superboot.repository.UserRepository;
import com.lhq.superboot.service.SMSTempService;
import com.lhq.superboot.util.StringUtils;
import com.lhq.superboot.vo.SMSTemplateVo;

/**
 * @Description: 短信模板接口实现(注意该类不能引用user模块无法获取userid ， 作为单独模块使用)
 * @author: lihaoqi
 * @date: 2019年6月11日 下午2:27:30
 * @version: v1.0.0
 */
@Service
public class SMSTempServiceImpl implements SMSTempService {

    @Autowired
    private SMSTemplateMapper smsTempMapper;

    @Autowired
    private SMSTemplateRepository smsTempRepo;

    @Autowired
    private UserSMSTemplateMapper userTempMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<SMSTemplate> selectByPage(PageInfo<SMSTemplateVo> pageInfo) {
        SMSTemplateExample example = new SMSTemplateExample();
        example.createCriteria().andIsDeletedEqualTo(IS_DELETE.NO.value()).andIsEnabledEqualTo(IS_ENABLED.YES.value())
                .andTemplateStateEqualTo(SMSTempState.AUDIT_PASS.getCode());

        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        return (Page<SMSTemplate>) smsTempMapper.selectByExample(example);
    }

    @Override
    public List<SMSTemplateVo> selectUserSMSTemplate(String userId) {
        UserManager userManager = userRepository.selectCurrentManager(userId);

        Map<String, Object> param = new HashMap<String, Object>(16);
        param.put("userId", userId);
        param.put("companyId", userManager.getManager().getCompanyId());
        return smsTempRepo.selectUserTemplate(param);
    }

    @Override
    @Transactional
    public String createTemplate(SMSTemplate template) {

        SMSTemplate.SMSTemplateBuilder build = new SMSTemplate().toBuilder();

        if (StringUtils.isNotEmpty(template.getTemplateCode())) {
            build.templateCode(template.getTemplateCode());
        }
        if (StringUtils.isNotEmpty(template.getTemplateFixParam())) {
            build.templateFixParam(template.getTemplateFixParam());
        }
        if (StringUtils.isNotEmpty(template.getRemark())) {
            build.remark(template.getRemark());
        }

        SMSTemplate tempParam = build.templateContent(template.getTemplateContent())
                .templateName(template.getTemplateName()).templateState(template.getTemplateState())
                .templateType(template.getTemplateType()).templateParamName(template.getTemplateParamName())
                .createUser(template.getCreateUser()).modifyUser(template.getCreateUser()).build();

        smsTempMapper.insertSelective(tempParam);
        return tempParam.getTemplateId();
    }

    @Override
    @Transactional
    public void createUserTemplate(SMSTemplate template, Integer isDefault) {
        // 先插入模板获取模板id
        String templateId = createTemplate(template);

        // 插入用户与模板的关联表
        assignedTemplate(template.getCreateUser(), templateId, template.getTemplateType(), isDefault);
    }

    @Override
    @Transactional
    public void approvalTemplate(SMSTemplate template) {

        SMSTemplate.SMSTemplateBuilder build = new SMSTemplate().toBuilder();

        if (StringUtils.isNotEmpty(template.getTemplateCode())) {
            build.templateCode(template.getTemplateCode());
        }
        if (StringUtils.isNotEmpty(template.getRemark())) {
            build.remark(template.getRemark());
        }

        SMSTemplate tempParam = build.templateState(template.getTemplateState()).modifyUser(template.getModifyUser()).build();
        smsTempMapper.updateByPrimaryKeySelective(tempParam);
    }

    @Override
    @Transactional
    public void assignedTemplate(String userId, String templateId, String templateType, Integer isDefault) {
        // 先判断是否已经存在该数据
        UserSMSTemplateExample example = new UserSMSTemplateExample();
        example.createCriteria().andTemplateIdEqualTo(templateId).andUserIdEqualTo(userId);
        List<UserSMSTemplate> userTempList = userTempMapper.selectByExample(example);
        if (userTempList.size() >= 1) {
            throw new SuperBootException("lhq-superboot-sms-0022");
        }

        UserSMSTemplate userTempParam = new UserSMSTemplate().toBuilder().userId(userId).isDefault(isDefault)
                .templateId(templateId).templateType(templateType).build();
        userTempMapper.insertSelective(userTempParam);
    }

    @Override
    @Transactional
    public void deleteTemplate(String userId, String templateId) {
        // 先查询是否存在用户使用该模板
        UserSMSTemplateExample userTempParam = new UserSMSTemplateExample();
        userTempParam.createCriteria().andTemplateIdEqualTo(templateId);
        List<UserSMSTemplate> userTempList = userTempMapper.selectByExample(userTempParam);
        // 存在用户在使用，无法删除
        if (!userTempList.isEmpty()) {
            throw new SuperBootException("lhq-superboot-sms-0013");
        }

        SMSTemplate tempParam = new SMSTemplate().toBuilder().templateId(templateId).isDeleted(IS_DELETE.YES.value())
                .modifyUser(userId).build();
        smsTempMapper.updateByPrimaryKeySelective(tempParam);
    }

    @Override
    @Transactional
    public void deleteUserTemplate(String userId, String templateId) {
        UserSMSTemplateExample userTempParam = new UserSMSTemplateExample();
        userTempParam.createCriteria().andUserIdEqualTo(userId).andTemplateIdEqualTo(templateId);
        userTempMapper.deleteByExample(userTempParam);
    }

    @Override
    @Transactional
    public void disableTemplate(String userId, String templateId) {
        // 禁用用户在使用的模板
        UserSMSTemplate userTempRecord = new UserSMSTemplate().toBuilder().isEnabled(IS_ENABLED.NO.value()).build();
        UserSMSTemplateExample userTempExample = new UserSMSTemplateExample();
        userTempExample.createCriteria().andTemplateIdEqualTo(templateId);
        userTempMapper.updateByExampleSelective(userTempRecord, userTempExample);
        // 禁用模板
        SMSTemplate tempRecord = new SMSTemplate().toBuilder().isEnabled(IS_ENABLED.NO.value()).templateId(templateId)
                .modifyUser(userId).build();
        smsTempMapper.updateByPrimaryKeySelective(tempRecord);
    }

    @Override
    @Transactional
    public void setTemplateDefault(String userId, String templateId, String templateType) {
        // 更新该类型对应的模板为默认模板，注意需要更新其余的为0-非默认
        Map<String, Object> param = new HashMap<String, Object>(3);
        param.put("userId", userId);
        param.put("templateId", templateId);
        param.put("templateType", templateType);
        smsTempRepo.updateUserDefaultTemplate(param);
    }

}
