package com.lhq.superboot.domain.menu;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Description: 小程序
 * @author: lihaoqi
 * @date: 2019年9月9日 下午4:41:58
 * @version: v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MiniproButton extends Button {

    private String url;
    private String appid;
    private String pagepath;

    @Builder
    public MiniproButton(String name, String type, List<Button> subButton, String url, String appid, String pagepath) {
        super(name, type, subButton);
        this.url = url;
        this.appid = appid;
        this.pagepath = pagepath;
    }
}
