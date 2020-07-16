package com.lhq.superboot.service;

import java.util.List;

import com.github.pagehelper.Page;
import com.lhq.superboot.domain.Resource;

/**
 * @Description: 资源信息
 * @author: lihaoqi
 * @date: 2019年5月6日
 * @version: 1.0.0
 */
public interface ResourceService {

    /**
     * @param resName
     * @param pageNum
     * @param pageSize
     * @return
     * @Description: 分页获取资源列表
     */
    public Page<Resource> selectResourceByPage(String resName, String resUrl, int pageNum, int pageSize);

    /**
     * @param res
     * @Description: 创建一个资源
     */
    public void createResource(Resource res);

    /**
     * @param res
     * @Description: 修改资源信息
     */
    public void updateResource(Resource res);

    /**
     * @param resIdList
     * @param isEnabled
     * @Description: 禁用资源
     */
    public void disableResource(List<String> resIdList, Integer isEnabled);

    /**
     * @param resIdList
     * @Description: 删除资源
     */
    public void deleteResource(List<String> resIdList);

    /**
     * @param roleId
     * @param pageNum
     * @param pageSize
     * @return
     * @Description: 获取某个角色下的资源
     */
    public Page<Resource> selectRoleRes(String roleId, int pageNum, int pageSize);

    /**
     * @param roleId
     * @param resIdList
     * @Description: 新增一个/多个资源权限对应关系
     */
    public void createRoleRes(String roleId, List<String> resIdList);

    /**
     * @param u
     * @param r
     * @Description: 通过url与roleId增加权限
     */
    public void createRoleRes(String url, String roleId);

    /**
     * @param roleResIdList
     * @param isEnabled
     * @Description: 禁用资源权限对应关系
     */
    public void disableRoleRes(List<String> roleResIdList, Integer isEnabled);

    /**
     * @param roleResIdList
     * @Description: 删除资源权限对应关系
     */
    public void deleteRoleRes(List<String> roleResIdList);

    /**
     * @param roleId
     * @param resIdList
     * @Description: 删除资源权限对应关系通过roleid与resid
     */
    public void deleteRoleRes(String roleId, List<String> resIdList);

}
