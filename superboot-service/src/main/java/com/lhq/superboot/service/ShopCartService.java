package com.lhq.superboot.service;

import java.util.List;

import com.github.pagehelper.Page;
import com.lhq.superboot.dto.ShopCartDto;
import com.lhq.superboot.vo.ShopCartVo;

/**
 * @Description: 购物车接口类
 * @author: lihaoqi
 * @date: 2019年7月7日 下午11:36:12
 * @version: v1.0.0
 */
public interface ShopCartService {

    /**
     * @param goodsId
     * @param goodsItemsId
     * @param goodsName
     * @param count
     * @return
     * @Description: 加入购物车
     */
    public void addShopCart(String goodsId, String goodsItemsId, String goodsName, Integer count);

    /**
     * @param goodsItemsIdList
     * @return
     * @Description: 删除购物车(批量)
     */
    public void deleteShopCart(List<String> goodsItemsIdList);

    /**
     * @param goodsName
     * @return
     * @Description: 模糊分页查询购物车(暂定)
     */
    public Page<ShopCartDto> queryShopCartByPage(String goodsName, int pageNum, int pageSize);

    /**
     * @return 返回商品详细信息
     * @Description: 查询用户全部购物车列表
     */
    public List<ShopCartDto> queryShopCart();

    /**
     * @param goodsItemsId
     * @param count        更改的数量
     * @return
     * @Description: 修改购物车内的商品数量
     */
    public void updateShopCartCount(String goodsItemsId, Integer count);

    /**
     * @param goodsId
     * @return
     * @Description: 是否已经加入购物车
     */
    public boolean isMyCarted(String goodsItemsId, String userId);

    /**
     * @param userId
     * @return
     * @Description: 查看用户购物车数量是否已经达到上限
     */
    public boolean isLimitLen(String userId);

    /**
     * @param userId
     * @return 返回redis中存储的购物车列表
     * @Description: 通过用户id获取用户全部的购物车列表
     */
    public List<ShopCartVo> getShopCartList(String userId);
}
