package com.lhq.superboot.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.ShopCartService;
import com.lhq.superboot.util.StringUtils;
import com.lhq.superboot.vo.ShopCartVo;

import io.swagger.annotations.Api;

/**
 * @Description: 购物车
 * @author: lihaoqi
 * @date: 2019年4月24日
 * @version: 1.0.0
 */
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/user/shopcart")
@Api(value = "购物车controller", tags = {"购物车操作接口"})
public class ShopCartController {

    @Autowired
    private ShopCartService shopCartService;

    /**
     * @return
     * @Description: 获取购物车（全部）
     */
    @GetMapping("/list")
    public Object getUserShopCart() {
        // 分为失效与未失效两块
        Map<String, Object> result = new HashMap<String, Object>(2);

        List<ShopCartVo> shopCartList = shopCartService.queryShopCart().stream().map(cart -> ShopCartVo.convert(cart))
                .collect(Collectors.toList());
        result.put("validGoods",
                shopCartList.stream().filter(cart -> cart.getIsVaild() == 1).collect(Collectors.toList()));
        result.put("inValidGoods",
                shopCartList.stream().filter(cart -> cart.getIsVaild() == 0).collect(Collectors.toList()));
        return result;
    }

    /**
     * @param shopCartVo
     * @return
     * @Description: 加入购物车
     */
    @PostMapping("/create")
    public Object createShopCart(@RequestBody ShopCartVo shopCartVo) {
        String goodsId = shopCartVo.getGoodsId();
        String goodsName = shopCartVo.getGoodsName();
        String goodsItemsId = shopCartVo.getGoodsItemsId();
        Integer count = shopCartVo.getCount();

        if (StringUtils.isEmpty(goodsId)) {
            throw new SuperBootException("lhq-superboot-shopcart-0002");
        }
        if (StringUtils.isEmpty(goodsName)) {
            throw new SuperBootException("lhq-superboot-shopcart-0004");
        }
        if (StringUtils.isEmpty(goodsItemsId)) {
            throw new SuperBootException("lhq-superboot-shopcart-0003");
        }
        if (count <= 0) {
            throw new SuperBootException("lhq-superboot-shopcart-0005");
        }

        shopCartService.addShopCart(goodsId, goodsItemsId, goodsName, count);
        return "新增购物车成功";
    }

    /**
     * @param goodsItemsIdList
     * @return
     * @Description: 删除购物车
     */
    @PostMapping("/delete")
    public Object deleteShopCart(@RequestBody List<String> goodsItemsIdList) {
        if (goodsItemsIdList.isEmpty()) {
            throw new SuperBootException("lhq-superboot-shopcart-0003");
        }

        shopCartService.deleteShopCart(goodsItemsIdList);
        return "移除购物车成功";
    }

    /**
     * @param shopCartVo
     * @return
     * @Description: 更新购物车的数量
     */
    @PostMapping("/updateCount")
    public Object updateShopCartCount(@RequestBody ShopCartVo shopCartVo) {
        String goodsItemsId = shopCartVo.getGoodsItemsId();
        Integer count = shopCartVo.getCount();

        if (StringUtils.isEmpty(goodsItemsId)) {
            throw new SuperBootException("lhq-superboot-shopcart-0003");
        }
        if (count <= 0) {
            throw new SuperBootException("lhq-superboot-shopcart-0005");
        }

        shopCartService.updateShopCartCount(goodsItemsId, count);
        return "修改购物车数目成功";
    }

}
