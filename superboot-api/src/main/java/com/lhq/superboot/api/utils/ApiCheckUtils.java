package com.lhq.superboot.api.utils;

import com.lhq.superboot.api.domain.ApiEntity;
import com.lhq.superboot.api.domain.ApiProp;
import com.lhq.superboot.api.enums.AsynType;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.UserApiService;
import com.lhq.superboot.util.DateConvertUtils;
import com.lhq.superboot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description: 对外接口工具类
 * @author: lihaoqi
 * @date: 2019/12/23 10:48
 * @version: v1.0.0
 */
@Component
public class ApiCheckUtils {

    private static final Logger logger = LoggerFactory.getLogger(ApiCheckUtils.class);

    /**
     * 时间格式
     **/
    private static final String TIME_FORMAT = "yyyyMMddHHmmssSSS";

    /**
     * 时间间隔校验ms(8秒内)
     **/
    private static final long TIME_SPACE = 1000 << 3;

    @Autowired
    private ApiProp apiProp;

    @Autowired
    private UserApiService userApiService;

    /**
     * @param apiEntity
     * @Description: 检查参数
     */
    public void checkParams(ApiEntity apiEntity) {
        // 基本参数校验
        if (StringUtils.isEmpty(apiEntity.getExeccode())) {
            throw new SuperBootException("lhq-superboot-api-0002");
        }
        if (StringUtils.isEmpty(apiEntity.getApikey())) {
            throw new SuperBootException("lhq-superboot-api-0003");
        }
        // 版本校验
        String version = apiEntity.getVersion();
        if (StringUtils.isEmpty(version)) {
            throw new SuperBootException("lhq-superboot-api-0004");
        }
        if (!apiProp.getVerison().equals(version)) {
            throw new SuperBootException("lhq-superboot-api-0005");
        }
        // 时间校验
        String timestamp = apiEntity.getTimestamp();
        if (StringUtils.isEmpty(timestamp)) {
            throw new SuperBootException("lhq-superboot-api-0006");
        }
        if (!checkTimestamp(timestamp)) {
            throw new SuperBootException("lhq-superboot-api-0007");
        }
        // 数据及其格式校验
        String format = apiEntity.getFormat();
        String data = apiEntity.getData();
        // 当数据不为空时才做数据格式校验
        if (data != null && StringUtils.isEmpty(format)) {
            throw new SuperBootException("lhq-superboot-api-0009");
        }
        if (!apiProp.getFormatArr().contains(format)) {
            throw new SuperBootException("lhq-superboot-api-0010", format);
        }
        // 异步回执校验
        int isAsyn = apiEntity.getIsAsyn();
        if (StringUtils.isEmpty(isAsyn)) {
            throw new SuperBootException("lhq-superboot-api-0011");
        }
        if (isAsyn == AsynType.ASYN.code() && StringUtils.isEmpty(apiEntity.getAsynRtnUrl())) {
            throw new SuperBootException("lhq-superboot-api-0012");
        }
        // 签名校验
        String apiSecret = userApiService.getApiSecret(apiEntity.getApikey());
        if (StringUtils.isEmpty(apiEntity.getSign())) {
            throw new SuperBootException("lhq-superboot-api-0013");
        }
        if (StringUtils.isEmpty(apiSecret)) {
            throw new SuperBootException("lhq-superboot-api-0015");
        }
        if (!checkSign(apiEntity, apiSecret)) {
            throw new SuperBootException("lhq-superboot-api-0014");
        }
    }

    /**
     * @param apiEntity
     * @return
     * @Description: 校验签名
     */
    private boolean checkSign(ApiEntity apiEntity, String apiSecret) {
        String sign = ApiSignUtils.generatorSign(apiEntity, apiSecret);
        return sign != null && sign.equals(apiEntity.getSign());
    }

    /**
     * @param timestamp
     * @return
     * @Description: 校验时间参数
     */
    private boolean checkTimestamp(String timestamp) {
        if (timestamp.length() != 17) {
            return false;
        }
        Date time = DateConvertUtils.toDateTime(timestamp, TIME_FORMAT);
        long startTime = time.getTime();
        long nowTime = new Date().getTime();
        logger.debug("[ApiCheckUtils] -> 访问时间：{}", (nowTime - startTime) / 1000);
        return nowTime - startTime < TIME_SPACE && nowTime - startTime >= 0;
    }

}
