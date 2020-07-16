package com.lhq.superboot.qo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mxb
 * @Description 入库参数接收
 * @date: 2019年7月8日 下午5:59:51
 * @version: 1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class InstockQo {

    private String warehouseId;
    private String supplierId;
    private List<InstockDetail> instockDetailList;
    private Integer goodsCategories;
    private int InstockType;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstockDetail {
        private String managerId;
        private String appOrderNo;
        private String goodsId;
        private String goodsItemsId;
        private BigDecimal price;
        private Long stock;
        private Long warnAmount;
        private Date expiryDate;
    }

}
