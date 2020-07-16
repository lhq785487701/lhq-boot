package com.lhq.superboot.service;

import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.lhq.superboot.domain.SMSTemplate;
import com.lhq.superboot.vo.SMSTemplateVo;

/**
 * @Description: 短信模块接口
 * @author: lihaoqi
 * @date: 2019年6月11日 下午2:19:41
 * @version: v1.0.0
 */
public interface SMSTempService {

    /**
     * @param pageInfo
     * @return
     * @Description: 分页查询短信模板列表
     */
    public Page<SMSTemplate> selectByPage(PageInfo<SMSTemplateVo> pageInfo);

    /**
     * @return
     * @Description: 查询用户所有的模板列表
     */
    public List<SMSTemplateVo> selectUserSMSTemplate(String userId);

    /**
     * @param template
     * @return 返回插入的模板id
     * @Description: 创建模板
     */
    public String createTemplate(SMSTemplate template);

    /**
     * @param template
     * @param userId
     * @param isDafault
     * @Description: 用户/公司创建模板
     */
    public void createUserTemplate(SMSTemplate template, Integer isDefault);

    /**
     * @param template
     * @Description: 审批模板
     */
    public void approvalTemplate(SMSTemplate template);

    /**
     * @param userId       指给的用户
     * @param templateId   模板id
     * @param templateType 模板类型
     * @Description: 指派模板给用户(插入usersmstemp表)
     */
    public void assignedTemplate(String userId, String templateId, String templateType, Integer isDefault);

    /**
     * @param userId     删除用户(作修改人)
     * @param templateId 删除的模板id
     * @Description: 删除模板(只支持单一删除 ， 并判断是否有用户在使用该模板 ， 如果存在则返回失败)
     */
    public void deleteTemplate(String userId, String templateId);

    /**
     * @param userId     删除的用户
     * @param templateId
     * @Description: 用户删除自己关联的模板(不删除模板)
     */
    public void deleteUserTemplate(String userId, String templateId);


    /**
     * @param userId     删除的用户
     * @param templateId
     * @Description: 禁用模板，注意需要禁用用户所使用的模板
     */
    public void disableTemplate(String userId, String templateId);

    /**
     * @param templateId
     * @param templateType
     * @Description: 用户设置默认模板
     */
    public void setTemplateDefault(String userId, String templateId, String templateType);

}
