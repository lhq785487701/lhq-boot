package com.lhq.superboot.service.impl;

import com.github.pagehelper.Page;
import com.lhq.superboot.dto.ShopCartDto;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.repository.ShopCartRepository;
import com.lhq.superboot.service.ShopCartService;
import com.lhq.superboot.service.UserService;
import com.lhq.superboot.util.FastJsonUtils;
import com.lhq.superboot.util.RedisUtils;
import com.lhq.superboot.util.StringUtils;
import com.lhq.superboot.vo.ShopCartVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 购物车实现
 * @author: lihaoqi
 * @date: 2019年7月8日 上午12:13:54
 * @version: v1.0.0
 */
@Service
public class ShopCartServiceImpl implements ShopCartService {

    private static final Logger logger = LoggerFactory.getLogger(ShopCartService.class);

    /**
     * 购物车redis存储前缀key
     **/
    private static final String SHOPPING_CART_KEY = "lhq:superboot:user:shoppingcart:";

    /**
     * 购物车最大数量
     **/
    private static final Integer SHOPPING_CART_MAX_LEN = 30;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ShopCartRepository shopCartRepository;

    @Override
    @Transactional
    public void addShopCart(String goodsId, String goodsItemsId, String goodsName, Integer count) {
        logger.debug("[ShopCartService] -> 用户加入购物车");
        String userId = getUserId();

        // 已加入购物车的商品排除(淘宝做法是移至表头)
        if (isMyCarted(goodsId, userId)) {
            logger.error("[ShopCartService] -> 该商品已经加入购物车");
            throw new SuperBootException("lhq-superboot-shopcart-0006", goodsName);
        }

        if (isLimitLen(userId)) {
            logger.error("[ShopCartService] -> 该用户购物车已达上限");
            throw new SuperBootException("lhq-superboot-shopcart-0008");
        }

        ShopCartVo shopCart = new ShopCartVo().toBuilder().goodsId(goodsId).goodsItemsId(goodsItemsId).count(count)
                .goodsName(goodsName).createTime(System.currentTimeMillis()).build();
        redisUtils.llSet(SHOPPING_CART_KEY + userId, FastJsonUtils.toJSONNoFeatures(shopCart));
    }

    @Override
    @Transactional
    public void deleteShopCart(List<String> goodsItemsIdList) {
        logger.debug("[ShopCartService] -> 删除购物车");
        String userId = getUserId();

        // 从list中获取需要删除的goodsItemsId，再转化json字符串
        List<String> shopCartList = getShopCartList(userId).stream()
                .filter(shopCart -> goodsItemsIdList.contains(shopCart.getGoodsItemsId()))
                .map(FastJsonUtils::toJSONNoFeatures).collect(Collectors.toList());

        if (shopCartList.isEmpty()) {
            logger.debug("[ShopCartService] -> 暂无商品需要删除：{}", goodsItemsIdList);
            throw new SuperBootException("lhq-superboot-shopcart-0007");
        }

        // 循环删除用户购物车
        for (String shopCartJson : shopCartList) {
            redisUtils.lRemove(SHOPPING_CART_KEY + userId, 0, shopCartJson);
        }
    }

    @Override
    public Page<ShopCartDto> queryShopCartByPage(String goodsName, int pageNum, int pageSize) {
        return null;
    }

    @Override
    public List<ShopCartDto> queryShopCart() {
        logger.debug("[ShopCartService] -> 查询购物车列表");
        String userId = getUserId();

        // 获取goodsItemsId
        List<ShopCartVo> shopCartVoList = getShopCartList(userId);

        List<ShopCartDto> goodsList = shopCartRepository.selectGoodsMsgByItemsId(
                shopCartVoList.stream().map(ShopCartVo::getGoodsItemsId).collect(Collectors.toList()));

        // 再次组合成list返回、注意顺序
        List<ShopCartDto> shopCartList = new ArrayList<>(30);
        for (ShopCartVo shopCartVo : shopCartVoList) {
            ShopCartDto shopCartDto = new ShopCartDto();
            shopCartDto.setCount(shopCartVo.getCount());

            for (ShopCartDto goodsItems : goodsList) {
                if (goodsItems.getGoodsItemsId().equals(shopCartVo.getGoodsItemsId())) {
                    ShopCartDto.setValue(shopCartDto, goodsItems);
                    break;
                }
            }
            shopCartList.add(shopCartDto);
        }
        return shopCartList;
    }

    @Override
    @Transactional
    public void updateShopCartCount(String goodsItemsId, Integer count) {
        logger.debug("[ShopCartService] -> 修改购物车数量");
        String userId = getUserId();

        List<ShopCartVo> shopCartList = getShopCartList(userId);

        // Id是否存在购物车、不存在抛出异常
        boolean isExist = false;
        for (int i = 0; i < shopCartList.size(); i++) {
            ShopCartVo shopCart = shopCartList.get(i);
            if (shopCart.getGoodsItemsId().equals(goodsItemsId)) {
                shopCart.setCount(count);
                redisUtils.lUpdateIndex(SHOPPING_CART_KEY + userId, i, FastJsonUtils.toJSONNoFeatures(shopCart));
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            logger.error("该物品不存在购物车中，goodsItemsId={}", goodsItemsId);
            throw new SuperBootException("lhq-superboot-shopcart-0001");
        }
    }

    @Override
    public boolean isMyCarted(String goodsItemsId, String userId) {
        List<ShopCartVo> shopCartList = getShopCartList(userId).stream()
                .filter(shopCart -> shopCart.getGoodsItemsId().equals(goodsItemsId)).collect(Collectors.toList());
        return shopCartList.size() == 1;
    }

    @Override
    public boolean isLimitLen(String userId) {
        long count = redisUtils.lGetListSize(SHOPPING_CART_KEY + userId);
        return count >= SHOPPING_CART_MAX_LEN;
    }

    @Override
    public List<ShopCartVo> getShopCartList(String userId) {
        List<Object> shopCartList = redisUtils.lGet(SHOPPING_CART_KEY + userId, 0, -1);

        return shopCartList.stream().map(shopCart -> FastJsonUtils.toBean((String) shopCart, ShopCartVo.class))
                .collect(Collectors.toList());
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
