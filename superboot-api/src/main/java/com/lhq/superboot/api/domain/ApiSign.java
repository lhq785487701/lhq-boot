package com.lhq.superboot.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @author: lihaoqi
 * @date: 2019/12/23 13:37
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ApiSign {

    private String excecode;
    private String apikey;
    private String timestamp;
    private String version;
}
