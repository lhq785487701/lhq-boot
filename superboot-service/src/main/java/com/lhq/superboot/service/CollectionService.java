package com.lhq.superboot.service;

import java.util.List;

import com.github.pagehelper.Page;
import com.lhq.superboot.domain.Goods;
import com.lhq.superboot.vo.CollectionVo;

/**
 * @Description: 收藏夹模块
 * @author: lihaoqi
 * @date: 2019年7月5日 上午1:26:49
 * @version: v1.0.0
 */
public interface CollectionService {

    /**
     * @param goodsId
     * @return
     * @Description: 加入一个商品进入收藏夹
     */
    public void addCollection(String goodsId, String goodsName);

    /**
     * @param goodsIdList
     * @return
     * @Description: 删除收藏夹(批量)
     */
    public void deleteCollection(List<String> goodsIdList);

    /**
     * @param goodsName
     * @return
     * @Description: 模糊分页查询收藏夹商品
     */
    public Page<Goods> queryCollectionByPage(String goodsName, int pageNum, int pageSize);

    /**
     * @param goodsId
     * @return
     * @Description: 该商品是否被当前登录用户所收藏
     */
    public boolean isCollected(String goodsId, String userId);

    /**
     * @param userId
     * @return
     * @Description: 通过用户id获取用户全部的收藏列表
     */
    public List<CollectionVo> getCollectList(String userId);

}
