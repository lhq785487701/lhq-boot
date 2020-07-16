package com.lhq.superboot.repository;

import com.lhq.superboot.dto.ShopCartDto;

import java.util.List;

/**
 * @Description: 购物车数据访问层
 * @author: lihaoqi
 * @date: 2019年7月8日 下午6:19:22
 * @version: v1.0.0
 */
public interface ShopCartRepository {

    /**
     * @param goodsItemsId goods_items表id
     * @return
     * @Description: 查询购物车信息
     */
    public List<ShopCartDto> selectGoodsMsgByItemsId(List<String> goodsItemsId);
}
