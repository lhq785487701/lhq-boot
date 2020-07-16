package com.lhq.superboot.vo;

import java.math.BigDecimal;

import com.lhq.superboot.dto.ShopCartDto;
import com.lhq.superboot.enums.ConstEnumUtils.IS_DELETE;
import com.lhq.superboot.enums.ConstEnumUtils.IS_ONLINE;
import com.lhq.superboot.util.BigDecimalUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 购物车Vo
 * @author: lihaoqi
 * @date: 2019年7月7日 上午2:31:38
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ShopCartVo {

    /**
     * 传入时参数
     **/
    private String goodsId;
    private String goodsItemsId;
    private String goodsName;
    private Integer count;
    private Long createTime;

    /**
     * 查询时参数
     **/
    private String goodsCode;
    private String goodsDesc;
    private String iconImage;
    private Integer goodsCategories;
    private String goodsItemsName;
    private String goodsItemsCode;
    private BigDecimal token;
    private BigDecimal salePrice;
    private Integer isVaild;                // 是否生效（删除或者下架的商品失效）

    private BigDecimal totalToken;            // 计算出的总token
    private BigDecimal totalSalePrice;        // 计算出的总销售价

    public static ShopCartVo convert(ShopCartDto shopCartDto) {
        int count = shopCartDto.getCount();
        BigDecimal countDe = new BigDecimal(count);
        BigDecimal token = BigDecimalUtils.toDecimal(shopCartDto.getTokenTotal());
        BigDecimal salePrice = BigDecimalUtils.toDecimal(shopCartDto.getSalePrice());
        int isOnline = shopCartDto.getIsOnline();
        int isDeleted = shopCartDto.getIsDeleted();
        int isVaild = 0;
        if (isDeleted == IS_DELETE.NO.value() && isOnline == IS_ONLINE.YES.value()) {
            isVaild = 1;
        }

        ShopCartVo shopCart = new ShopCartVo().toBuilder()
                .goodsId(shopCartDto.getGoodsId())
                .goodsName(shopCartDto.getGoodsName())
                .goodsCode(shopCartDto.getGoodsCode())
                .goodsDesc(shopCartDto.getGoodsDesc())
                .goodsCategories(shopCartDto.getGoodsCategories())
                .goodsItemsId(shopCartDto.getGoodsItemsId())
                .goodsItemsCode(shopCartDto.getGoodsItemsCode())
                .goodsItemsName(shopCartDto.getGoodsItemsName())
                .iconImage(shopCartDto.getIconImage())
                .count(count)
                .isVaild(isVaild)
                .totalToken(token)
                .salePrice(salePrice)
                .totalToken(token.multiply(countDe))
                .totalSalePrice(salePrice.multiply(countDe)).build();

        return shopCart;
    }

}
