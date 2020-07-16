package com.lhq.superboot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OfficialMenu {
    private String menuId;

    private String menuPid;

    private String menuType;

    private String menuName;

    private String menuKey;

    private String menuUrl;

    private String mediaId;

    private String appid;

    private String pagepath;

}