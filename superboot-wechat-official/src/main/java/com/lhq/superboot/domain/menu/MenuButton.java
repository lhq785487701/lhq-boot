package com.lhq.superboot.domain.menu;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 菜单实体类
 * @author: lihaoqi
 * @date: 2019年9月9日 上午11:00:13
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MenuButton {

    private List<Button> button;
}
