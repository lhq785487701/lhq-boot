package com.lhq.superboot.api.domain;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description:
 * @author: lihaoqi
 * @date: 2019/12/23 10:58
 * @version: v1.0.0
 */
@Component
@Configuration
@ConfigurationProperties(prefix = "api")
@Data
public class ApiProp {

    private List<String> formatArr;

    @Value("${api.version}")
    private String verison;
}
