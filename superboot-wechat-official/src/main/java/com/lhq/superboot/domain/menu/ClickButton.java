package com.lhq.superboot.domain.menu;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Description: 菜单点击类型
 * @author: lihaoqi
 * @date: 2019年9月9日 上午10:56:47
 * @version: v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ClickButton extends Button {

    /**
     * 菜单KEY值
     **/
    private String key;

    @Builder
    public ClickButton(String name, String type, List<Button> subButton, String key) {
        super(name, type, subButton);
        this.key = key;
    }
}
