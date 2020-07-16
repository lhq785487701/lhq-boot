package com.lhq.superboot.service.impl;

import com.github.pagehelper.Page;
import com.lhq.superboot.domain.Goods;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.CollectionService;
import com.lhq.superboot.service.UserService;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.RedisUtils;
import com.lhq.superboot.util.StringUtils;
import com.lhq.superboot.vo.CollectionVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 收藏夹实现
 * @author: lihaoqi
 * @date: 2019年7月5日 上午1:27:13
 * @version: v1.0.0
 */
@Service
public class CollectionServiceImpl implements CollectionService {

    private static final Logger logger = LoggerFactory.getLogger(CollectionService.class);

    /**
     * 收藏夹redis存储前缀key
     **/
    private static final String COLLECT_KEY = "lhq:superboot:user:collection:";

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtils redisUtils;


    @Override
    public void addCollection(String goodsId, String goodsName) {
        logger.debug("[CollectionService] -> 用户加入收藏");
        String userId = getUserId();
        // 已经收藏的商品排除
        if (isCollected(goodsId, userId)) {
            logger.debug("[CollectionService] -> 商品已经收藏");
            throw new SuperBootException("lhq-superboot-collect-0003");
        }

        CollectionVo collect = new CollectionVo().toBuilder().goodsId(goodsId).goodsName(goodsName)
                .createTime(System.currentTimeMillis()).build();
        redisUtils.llSet(COLLECT_KEY + userId, FastJsonUtils.toJSONNoFeatures(collect));
    }

    @Override
    public void deleteCollection(List<String> goodsIdList) {
        logger.debug("[CollectionService] -> 删除收藏");
        String userId = getUserId();

        // 从list中获取需要删除的goodsId，再转化json字符串
        List<String> collectList = getCollectList(userId).stream()
                .filter(collect -> goodsIdList.contains(collect.getGoodsId()))
                .map(FastJsonUtils::toJSONNoFeatures).collect(Collectors.toList());

        if (collectList.isEmpty()) {
            logger.debug("[CollectionService] -> 暂无商品需要删除：{}", goodsIdList);
            throw new SuperBootException("lhq-superboot-collect-0004");
        }

        // 循环删除用户收藏夹信息、通过值collectJson
        for (String collectJson : collectList) {
            redisUtils.lRemove(COLLECT_KEY + userId, 0, collectJson);
        }
    }

    @Override
    public Page<Goods> queryCollectionByPage(String goodsName, int pageNum, int pageSize) {
        logger.debug("[CollectionService] -> 分页模糊查询收藏夹");
        String userId = getUserId();

        List<String> collectIdPage;
        // 如果查询条件为空，直接redis分页查询
        if (StringUtils.isEmpty(goodsName)) {
            // redis查询不存在数组越界错误
            int startNum = (pageNum - 1) * pageSize;
            int endNum = startNum + pageSize - 1;
            List<Object> collectList = redisUtils.lGet(COLLECT_KEY + userId, startNum, endNum);
            collectIdPage = collectList.stream()
                    .map(collect -> FastJsonUtils.toBean((String) collect, CollectionVo.class))
                    .map(CollectionVo::getGoodsId).collect(Collectors.toList());
        } else {
            // 先模糊查询所有名称为goodsName的值，并获取id即可
            List<String> collectIdList = getCollectList(userId).stream()
                    .filter(collect -> collect.getGoodsName().contains(goodsName)).map(CollectionVo::getGoodsId)
                    .collect(Collectors.toList());
            // 获取分页索引信息，注意subList的toIndex属性默认减一，如[0,1,2,3] subList(3,4)=[4]
            int collectListSize = collectIdList.size();
            int startNum = (pageNum - 1) * pageSize;
            int endNum = startNum + pageSize;
            int realEndNum = endNum > collectListSize ? collectListSize : endNum;

            // 做判断为防subList数组越界
            if (startNum >= collectListSize) {
                logger.error("[CollectionService] -> 超过页数上限");
                return null;
            }
            // 获取最终分页数据
            collectIdPage = collectIdList.subList(startNum, realEndNum);
        }

        if (collectIdPage.isEmpty()) {
            return null;
        }

        // 获取商品信息（查询sql）
        Page<Goods> goodsPage = new Page<>();
        goodsPage.setTotal(getCollectListLen(userId));
        return goodsPage;
    }

    @Override
    public boolean isCollected(String goodsId, String userId) {
        logger.debug("[CollectionService] -> 判断用户是否收藏");
        List<CollectionVo> collectList = getCollectList(userId).stream()
                .filter(collect -> collect.getGoodsId().equals(goodsId)).collect(Collectors.toList());
        return collectList.size() == 1;
    }

    @Override
    public List<CollectionVo> getCollectList(String userId) {
        List<Object> collectList = redisUtils.lGet(COLLECT_KEY + userId, 0, -1);

        return collectList.stream().map(collect -> FastJsonUtils.toBean((String) collect, CollectionVo.class))
                .collect(Collectors.toList());
    }

    /**
     * @param userId
     * @return
     * @Description: 获取用户收藏总数
     */
    private Long getCollectListLen(String userId) {
        return redisUtils.llen(COLLECT_KEY + userId);
    }

    /**
     * @return userId
     * @Description: 获取用户id
     */
    private String getUserId() {
        String userId = userService.getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new SuperBootException("lhq-superboot-user-0021");
        }
        return userId;
    }

}
