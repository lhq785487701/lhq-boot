package com.lhq.superboot.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @author: lihaoqi
 * @date: 2019/12/23 10:30
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ApiEntity {

    private String execcode;
    private String apikey;
    private String timestamp;
    private String version;
    private String sign;
    private String format;
    private String data;
    private int isAsyn;
    private String asynRtnUrl;

}
