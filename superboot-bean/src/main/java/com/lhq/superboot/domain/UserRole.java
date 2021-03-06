package com.lhq.superboot.domain;

/**
 * @Description: 用户权限实体类
 * @author: lihaoqi
 * @version: 1.0.0
 * @date: 2019年4月17日
 */
public class UserRole extends User {

    private Role role;

    private Channel channel;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
