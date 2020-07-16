package com.lhq.superboot.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: xcx用户信息实体类
 * @author: lihaoqi
 * @date: 2019年4月25日
 * @version: 1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserConsumer {

    private User user;

    private Consumer consumer;
}
