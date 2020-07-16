package com.lhq.superboot.domain.menu;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Description: 图片按钮
 * @author: lihaoqi
 * @date: 2019年9月9日 下午4:36:25
 * @version: v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PicButton extends Button {

    private String key;

    @Builder
    public PicButton(String name, String type, List<Button> subButton, String key) {
        super(name, type, subButton);
        this.key = key;
    }
}
