package com.lhq.superboot.qo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 收货地址查询条件
 * @author: lihaoqi
 * @date: 2019年7月17日 上午11:27:59
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressQo {

    private String addrName;
    private String receiverName;
    private String province;
    private String city;
    private String area;
    private String addrLocation;
}
