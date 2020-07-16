package com.lhq.superboot.domain.menu;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Description: 菜单链接按钮
 * @author: lihaoqi
 * @date: 2019年9月9日 上午10:59:17
 * @version: v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ViewButton extends Button {

    private String url;

    @Builder
    public ViewButton(String name, String type, List<Button> subButton, String url) {
        super(name, type, subButton);
        this.url = url;
    }

}
