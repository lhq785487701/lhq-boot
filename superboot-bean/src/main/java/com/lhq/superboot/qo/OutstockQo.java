package com.lhq.superboot.qo;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mxb
 * @Description 出库商品参数接收类
 * @date: 2019年7月8日 下午7:55:06
 * @version: 1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OutstockQo {

    private String customerId;
    private Integer outStockType;
    private List<OutstockDetail> outstockDetailList;
    private BigDecimal totalPrice;

    @Data
    @Builder(toBuilder = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutstockDetail {
        private String goodsId;
        private String goodsItemsId;
        private Long count;
        private BigDecimal price;
    }
}
