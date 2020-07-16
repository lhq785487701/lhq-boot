package com.lhq.superboot.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lhq.superboot.domain.OfficialMenu;
import com.lhq.superboot.domain.OfficialMenuExample;
import com.lhq.superboot.domain.menu.Button;
import com.lhq.superboot.domain.menu.ClickButton;
import com.lhq.superboot.domain.menu.MediaButton;
import com.lhq.superboot.domain.menu.MenuButton;
import com.lhq.superboot.domain.menu.MiniproButton;
import com.lhq.superboot.domain.menu.PicButton;
import com.lhq.superboot.domain.menu.ViewButton;
import com.lhq.superboot.enums.OfficialMenuType;
import com.lhq.superboot.mapper.OfficialMenuMapper;
import com.lhq.superboot.property.WxOfficialProperty;
import com.lhq.superboot.service.OfficialApiService;
import com.lhq.superboot.service.OfficialMenuService;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.HttpClientUtils;
import com.lhq.superboot.util.StringUtils;

/**
 * @Description: 公众号菜单实现类
 * @author: lihaoqi
 * @date: 2019年9月9日 上午11:20:56
 * @version: v1.0.0
 */
@Service
public class OfficialMenuServiceImpl implements OfficialMenuService {

    private static final Logger logger = LoggerFactory.getLogger(OfficialMenuService.class);

    @Autowired
    private WxOfficialProperty wxOffProperty;

    @Autowired
    private OfficialApiService offApi;

    @Autowired
    private OfficialMenuMapper menuMapper;

    @Override
    public MenuButton initMenu() {
        logger.debug("[OfiicialMenuService] -> 初始化菜单");
        OfficialMenuExample example = new OfficialMenuExample();
        List<OfficialMenu> menuList = menuMapper.selectByExample(example);
        if (menuList.isEmpty()) {
            logger.warn("[OfiicialMenuService] -> 暂无菜单信息");
            return null;
        }
        List<OfficialMenu> pMenuList = menuList.stream().filter(menu -> StringUtils.isEmpty(menu.getMenuPid()))
                .collect(Collectors.toList());
        int pSize = pMenuList.size();
        if (pSize == 0) {
            logger.warn("[OfiicialMenuService] -> 暂无父节点菜单信息");
            return null;
        }
        if (pSize > 3) {
            logger.warn("[OfiicialMenuService] -> 父级菜单大于3个，菜单将丢失大于3的");
            pMenuList.subList(0, 3);
        }
        List<Button> buttonArr = new ArrayList<>(pSize);
        for (int i = 0; i < pMenuList.size(); i++) {
            buttonArr.add(getButtonMsg(pMenuList.get(i)));
        }

        for (int i = 0; i < menuList.size(); i++) {
            OfficialMenu menu = menuList.get(i);
            String pid = menu.getMenuPid();
            if (StringUtils.isEmpty(menu.getMenuPid())) {
                continue;
            }
            for (int j = 0; j < pMenuList.size(); j++) {
                if (pMenuList.get(j).getMenuId().equals(pid)) {
                    List<Button> subButton = buttonArr.get(j).getSub_button();
                    if (null == subButton) {
                        subButton = new ArrayList<>(5);
                        buttonArr.get(j).setSub_button(subButton);
                    } else if (subButton.size() > 5) {
                        logger.warn("[OfiicialMenuService] -> 子级菜单不超过5个，丢失菜单{}", menu.getMenuName());
                        break;
                    }
                    subButton.add(getButtonMsg(menu));
                    break;
                }
            }
        }
        logger.debug("[OfiicialMenuService] -> 菜单初始化结果{}", buttonArr);
        return new MenuButton().toBuilder().button(buttonArr).build();
    }

    private Button getButtonMsg(OfficialMenu menu) {
        String type = menu.getMenuType();
        String name = menu.getMenuName();
        if (OfficialMenuType.CLICK.getCode().equals(type)) {
            return ClickButton.builder().key(menu.getMenuKey()).type(type).name(name).build();
        } else if (OfficialMenuType.VIEW.getCode().equals(type)) {
            return ViewButton.builder().url(menu.getMenuUrl()).type(type).name(name).build();
        } else if (OfficialMenuType.MINIPROGRAM.getCode().equals(type)) {
            return MiniproButton.builder().url(menu.getMenuUrl()).type(type).name(name).appid(menu.getAppid())
                    .pagepath(menu.getPagepath()).build();
        } else if (OfficialMenuType.MEDIA_ID.getCode().equals(type)
                || OfficialMenuType.VIEW_LIMITED.getCode().equals(type)) {
            return MediaButton.builder().mediaId(menu.getMediaId()).type(type).name(name).build();
        } else if (OfficialMenuType.PIC_PHOTO_OR_ALBUM.getCode().equals(type)
                || OfficialMenuType.PIC_SYSPHOTO.getCode().equals(type)
                || OfficialMenuType.PIC_WEIXIN.getCode().equals(type)) {
            return PicButton.builder().key(menu.getMenuKey()).type(type).name(name).build();
        } else {
            return ClickButton.builder().key(menu.getMenuKey()).name(name).build();
        }
    }

    @Override
    public boolean creatMenu(MenuButton menu) {
        logger.debug("[OfiicialMenuService] -> 创建公众号菜单");
        boolean result = false;
        String accessToken = offApi.getAccessToken();
        String menuJson = FastJsonUtils.toJSONNoFeatures(menu);
        String url = wxOffProperty.getCreateMenuUrl().replace("ACCESS_TOKEN", accessToken);

        logger.debug("[OfiicialMenuService] -> 公众号菜单json ：{}", menuJson);
        JSONObject jsonObject = HttpClientUtils.httpPost(url, menuJson);
        if (null != jsonObject) {
            int errorCode = jsonObject.getIntValue("errcode");
            String errorMsg = jsonObject.getString("errmsg");
            if (0 == errorCode) {
                logger.debug("[OfiicialMenuService] -> 创建菜单成功 ");
                return true;
            } else {
                logger.error("[OfiicialMenuService] -> 创建菜单失败 errcode：{} errmsg：{} ", errorCode, errorMsg);
            }
        }
        return result;
    }

    @Override
    public String getMenu() {
        logger.debug("[OfiicialMenuService] -> 查询公众号菜单");
        String accessToken = offApi.getAccessToken();
        String url = wxOffProperty.getQueryMenuUrl().replace("ACCESS_TOKEN", accessToken);

        JSONObject jsonObject = HttpClientUtils.httpGet(url);
        if (null != jsonObject) {
            return jsonObject.toString();
        }
        return null;
    }

    @Override
    public boolean deleteMenu() {
        logger.debug("[OfiicialMenuService] -> 删除公众号菜单");
        boolean result = false;
        String accessToken = offApi.getAccessToken();
        String url = wxOffProperty.getDeleteMenuUrl().replace("ACCESS_TOKEN", accessToken);
        JSONObject jsonObject = HttpClientUtils.httpGet(url);
        if (null != jsonObject) {
            int errorCode = jsonObject.getIntValue("errcode");
            String errorMsg = jsonObject.getString("errmsg");
            if (0 == errorCode) {
                logger.debug("删除菜单成功 ");
                return true;
            } else {
                logger.error("删除菜单失败 errcode：{} errmsg：{} ", errorCode, errorMsg);
            }
        }
        return result;
    }

}
