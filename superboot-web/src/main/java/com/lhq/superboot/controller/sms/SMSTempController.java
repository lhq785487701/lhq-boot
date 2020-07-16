package com.lhq.superboot.controller.sms;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.lhq.superboot.domain.SMSTemplate;
import com.lhq.superboot.enums.SMSTempState;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.SMSTempService;
import com.lhq.superboot.service.UserService;
import com.lhq.superboot.util.StringUtils;
import com.lhq.superboot.vo.SMSTemplateVo;

import io.swagger.annotations.Api;

/**
 * @author 作者 lihaoqi
 * @version 创建时间：2019年6月11日 下午1:43:38
 * @Description: 用户短信模块
 */
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/sms")
@Api(value = "短信模板controller", tags = {"短信模板操作接口"})
public class SMSTempController {

    @Autowired
    private UserService userService;

    @Autowired
    private SMSTempService smsTempService;

    /**
     * 分页查询短信模板列表(后台查询模板功能)
     *
     * @param pageInfo
     * @return
     */
    @GetMapping("/template/ht/list")
    public Object getSMSTemplatePage(PageInfo<SMSTemplateVo> pageInfo) {
        Page<SMSTemplate> smsTempPage = smsTempService.selectByPage(pageInfo);

        pageInfo.setList(
                smsTempPage.getResult().stream().map(s -> SMSTemplateVo.convert(s)).collect(Collectors.toList()));
        pageInfo.setTotal(smsTempPage.getTotal());
        return pageInfo;
    }

    /**
     * 查询用户所有的模板列表(pc查询当前用户的模板)
     *
     * @param pageInfo
     * @return
     */
    @GetMapping("/template/pc/list")
    public Object getUserSMSTemplateList() {

        List<SMSTemplateVo> smsTempList = smsTempService.selectUserSMSTemplate(getUserId());

        return smsTempList;
    }

    /**
     * @param template
     * @return
     * @Description: 后台创建模板
     */
    @PostMapping("/template/ht/create")
    public Object createTemplate(@RequestBody SMSTemplate template) {
        if (StringUtils.isEmpty(template.getTemplateContent())) {
            throw new SuperBootException("lhq-superboot-sms-0014");
        }
        if (StringUtils.isEmpty(template.getTemplateName())) {
            throw new SuperBootException("lhq-superboot-sms-0016");
        }
        if (StringUtils.isEmpty(template.getTemplateParamName())) {
            throw new SuperBootException("lhq-superboot-sms-0015");
        }
        if (StringUtils.isEmpty(template.getTemplateCode())) {
            throw new SuperBootException("lhq-superboot-sms-0019");
        }
        if (StringUtils.isEmpty(template.getTemplateType())) {
            throw new SuperBootException("lhq-superboot-sms-0017");
        }

        // 管理员创建的为通过的模板
        template.setTemplateState(SMSTempState.AUDIT_PASS.getCode());
        template.setCreateUser(getUserId());

        return smsTempService.createTemplate(template);
    }

    /**
     * @param templateVo
     * @return
     * @Description: pc用户申请创建模板
     */
    @PostMapping("/template/pc/create")
    public Object createUserTemplate(@RequestBody SMSTemplateVo templateVo) {
        String templateContent = templateVo.getTemplateContent();
        String templateName = templateVo.getTemplateName();
        String templateParamName = templateVo.getTemplateParamName();
        String templateType = templateVo.getTemplateType();
        Integer isDefault = templateVo.getIsDefault();
        if (StringUtils.isEmpty(templateContent)) {
            throw new SuperBootException("lhq-superboot-sms-0014");
        }
        if (StringUtils.isEmpty(templateName)) {
            throw new SuperBootException("lhq-superboot-sms-0016");
        }
        if (StringUtils.isEmpty(templateParamName)) {
            throw new SuperBootException("lhq-superboot-sms-0015");
        }
        if (StringUtils.isEmpty(templateType)) {
            throw new SuperBootException("lhq-superboot-sms-0017");
        }

        // 默认未通过等审批
        SMSTemplate template = new SMSTemplate().toBuilder().createUser(getUserId()).templateContent(templateContent)
                .templateType(templateType).templateState(SMSTempState.AUDITING.getCode())
                .templateParamName(templateParamName).templateName(templateName).build();
        smsTempService.createUserTemplate(template, isDefault);
        return "创建用户模板成功";
    }

    /**
     * @param template
     * @return
     * @Description: 后台审批模板
     */
    @PostMapping("/template/ht/audit")
    public Object auditTemplate(@RequestBody SMSTemplate template) {
        Integer templateState = template.getTemplateState();
        String remark = template.getRemark();
        String templateCode = template.getTemplateCode();
        // 不通过需要写入原因、通过需要写入模板code
        if (SMSTempState.AUDIT_NOT_PASS.getCode() == templateState) {
            if (StringUtils.isEmpty(remark)) {
                throw new SuperBootException("lhq-superboot-sms-0019");
            }
        } else if (SMSTempState.AUDIT_PASS.getCode() == templateState) {
            if (StringUtils.isEmpty(templateCode)) {
                throw new SuperBootException("lhq-superboot-sms-0020");
            }
        }
        template.setModifyUser(getUserId());
        smsTempService.approvalTemplate(template);
        return "审批成功";
    }

    /**
     * @param templateVo
     * @return
     * @Description: 后台将模板指派给用户
     */
    @PostMapping("/template/ht/assign")
    public Object assignedTemJplate(@RequestBody SMSTemplateVo templateVo) {
        String userId = templateVo.getUserId();
        Integer isDefault = templateVo.getIsDefault();
        String templateId = templateVo.getTemplateId();
        String templateType = templateVo.getTemplateType();

        if (StringUtils.isEmpty(templateId)) {
            throw new SuperBootException("lhq-superboot-sms-0021");
        }
        if (StringUtils.isEmpty(templateType)) {
            throw new SuperBootException("lhq-superboot-sms-0017");
        }
        if (StringUtils.isEmpty(userId)) {
            throw new SuperBootException("lhq-superboot-user-0015");
        }
        if (isDefault == null) {
            isDefault = 0;
        }
        smsTempService.assignedTemplate(userId, templateId, templateType, isDefault);

        return "指派成功";
    }

    /**
     * @param templateVo
     * @return
     * @Description: 后台将模板删除(注意如果有用户使用该模板无法删除)
     */
    @PostMapping("/template/ht/delete")
    public Object deleteTemJplate(@RequestBody SMSTemplate template) {
        String templateId = template.getTemplateId();

        if (StringUtils.isEmpty(templateId)) {
            throw new SuperBootException("lhq-superboot-sms-0021");
        }
        smsTempService.deleteTemplate(getUserId(), templateId);

        return "删除模板成功";
    }

    /**
     * @param template
     * @return
     * @Description: 后台将模板删除(注意如果有用户使用该模板无法删除)
     */
    @PostMapping("/template/pc/delete")
    public Object deleteUserTemJplate(@RequestBody SMSTemplate template) {
        String templateId = template.getTemplateId();

        if (StringUtils.isEmpty(templateId)) {
            throw new SuperBootException("lhq-superboot-sms-0021");
        }
        smsTempService.deleteUserTemplate(getUserId(), templateId);

        return "删除成功";
    }

    /**
     * @param template
     * @return
     * @Description: 后台将禁用模板
     */
    @PostMapping("/template/ht/disable")
    public Object disableTemplate(@RequestBody SMSTemplate template) {
        String templateId = template.getTemplateId();

        if (StringUtils.isEmpty(templateId)) {
            throw new SuperBootException("lhq-superboot-sms-0021");
        }
        smsTempService.disableTemplate(getUserId(), templateId);

        return "删除成功";
    }

    /**
     * @param template
     * @return
     * @Description: 设置模板为该类型的默认模板
     */
    @PostMapping("/template/pc/setDefault")
    public Object setDefaultTemplate(@RequestBody SMSTemplate template) {
        String templateId = template.getTemplateId();
        String templateType = template.getTemplateType();

        if (StringUtils.isEmpty(templateId)) {
            throw new SuperBootException("lhq-superboot-sms-0021");
        }
        if (StringUtils.isEmpty(templateType)) {
            throw new SuperBootException("lhq-superboot-sms-0017");
        }
        smsTempService.setTemplateDefault(getUserId(), templateId, templateType);

        return "删除成功";
    }

    /**
     * @return userId
     * @Description: 获取用户id
     */
    private String getUserId() {
        String userId = userService.getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new SuperBootException("lhq-superboot-user-0021");
        }
        return userId;
    }

}
