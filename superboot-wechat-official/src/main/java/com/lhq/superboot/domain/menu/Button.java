package com.lhq.superboot.domain.menu;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 按钮的基类（每个按钮对象都有一个共同的name属性，因此需要定义一个按钮对象的基类，所有按钮对象都需要继承该类）
 * @author: lihaoqi
 * @date: 2019年9月9日 上午10:50:48
 * @version: v1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Button {

    /**
     * 菜单标题
     **/
    private String name;

    /**
     * 菜单的响应动作类型
     **/
    private String type;

    /**
     * 子菜单
     **/
    private List<Button> sub_button;

}
