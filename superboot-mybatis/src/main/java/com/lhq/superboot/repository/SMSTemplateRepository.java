package com.lhq.superboot.repository;

import java.util.List;
import java.util.Map;

import com.lhq.superboot.vo.SMSTemplateVo;

/**
 * @Description: 短信模板数据访问层
 * @author: lihaoqi
 * @date: 2019年6月18日 上午11:21:32
 * @version: v1.0.0
 */
public interface SMSTemplateRepository {

    /**
     * @param param
     * @return 返回模板列表
     * @Description: 查询用户短信模板
     */
    public List<SMSTemplateVo> selectUserTemplate(Map<String, Object> param);

    /**
     * @param param (userId/templateId/templateType)
     * @Description: 更新用户某个类型的短信模板为默认模板
     */
    public void updateUserDefaultTemplate(Map<String, Object> param);
}
