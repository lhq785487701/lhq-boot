package com.lhq.superboot.controller.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.lhq.superboot.domain.Goods;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.service.CollectionService;
import com.lhq.superboot.util.StringUtils;
import com.lhq.superboot.vo.CollectionVo;

import io.swagger.annotations.Api;

/**
 * @Description: 收藏夹
 * @author: lihaoqi
 * @date: 2019年7月8日
 * @version: 1.0.0
 */
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/user/collect")
@Api(value = "收藏夹接口controller", tags = {"收藏夹操作接口"})
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    /**
     * @param goodsName
     * @param page
     * @return
     * @Description: 查询用户收藏夹
     */
    @GetMapping("/list")
    public Object getUserCollection(String goodsName, PageInfo<CollectionVo> page) {
        Page<Goods> collectPage = collectionService.queryCollectionByPage(goodsName, page.getPageNum(),
                page.getPageSize());
        page.setList(collectPage.getResult().stream().map(collect -> CollectionVo.convert(collect))
                .collect(Collectors.toList()));
        page.setTotal(collectPage.getTotal());
        return page;
    }

    /**
     * @param collectionVo
     * @return
     * @Description: 加入收藏夹
     */
    @PostMapping("/create")
    public Object createCollection(@RequestBody CollectionVo collectionVo) {
        String goodsId = collectionVo.getGoodsId();
        String goodsName = collectionVo.getGoodsName();
        if (StringUtils.isEmpty(goodsId)) {
            throw new SuperBootException("lhq-superboot-collect-0001");
        }
        if (StringUtils.isEmpty(goodsName)) {
            throw new SuperBootException("lhq-superboot-collect-0002");
        }

        collectionService.addCollection(goodsId, goodsName);
        return "新增收藏夹成功";
    }

    /**
     * @param goodsIdList
     * @return
     * @Description: 批量移除收藏夹
     */
    @PostMapping("/delete")
    public Object deleteCollection(@RequestBody List<String> goodsIdList) {
        if (goodsIdList.isEmpty()) {
            throw new SuperBootException("lhq-superboot-collect-0001");
        }

        collectionService.deleteCollection(goodsIdList);
        return "移除收藏夹成功";
    }

}
