package com.lhq.superboot.domain.menu;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Description: 视频等按钮
 * @author: lihaoqi
 * @date: 2019年9月9日 下午4:46:16
 * @version: v1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MediaButton extends Button {

    private String mediaId;

    @Builder
    public MediaButton(String name, String type, List<Button> subButton, String mediaId) {
        super(name, type, subButton);
        this.mediaId = mediaId;
    }
}
