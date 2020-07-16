package com.lhq.superboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 购物车dto
 * @author: lihaoqi
 * @date: 2019年7月8日 下午5:58:37
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ShopCartDto {

    private String goodsId;
    private String goodsName;
    private String goodsCode;
    private String goodsDesc;
    private Integer goodsCategories;
    private String iconImage;
    private String goodsItemsId;
    private String goodsItemsName;
    private String goodsItemsCode;
    private Long tokenTotal;
    private Long salePrice;
    private Integer count;
    private Integer isOnline;
    private Integer isDeleted;

    public static ShopCartDto setValue(ShopCartDto shopCart, ShopCartDto shopCartParam) {
        shopCart.setGoodsCode(shopCartParam.getGoodsCode());
        shopCart.setGoodsDesc(shopCartParam.getGoodsDesc());
        shopCart.setGoodsId(shopCartParam.getGoodsId());
        shopCart.setGoodsName(shopCartParam.getGoodsName());
        shopCart.setGoodsCategories(shopCartParam.getGoodsCategories());
        shopCart.setIconImage(shopCartParam.getIconImage());
        shopCart.setGoodsItemsId(shopCartParam.getGoodsItemsId());
        shopCart.setGoodsItemsName(shopCartParam.getGoodsItemsName());
        shopCart.setGoodsItemsCode(shopCartParam.getGoodsItemsCode());
        shopCart.setTokenTotal(shopCartParam.getTokenTotal());
        shopCart.setSalePrice(shopCartParam.getSalePrice());
        shopCart.setIsOnline(shopCartParam.getIsOnline());
        shopCart.setIsDeleted(shopCartParam.getIsDeleted());
        return shopCart;
    }


}
