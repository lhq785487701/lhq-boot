package com.lhq.superboot.vo;

import com.lhq.superboot.domain.Goods;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 收藏夹Vo
 * @author: lihaoqi
 * @date: 2019年7月5日 下午6:31:38
 * @version: v1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CollectionVo {

    /**
     * 加入收藏夹时传入
     **/
    private String goodsId;
    private String goodsName;
    private Long createTime;

    /**
     * 查询参数
     **/
    private String goodsCode;
    private String goodsDesc;
    private String iconImage;
    private Integer isOnline;
    private Integer isDeleted;

    public static CollectionVo convert(Goods goods) {
        CollectionVo collectMsg = new CollectionVo().toBuilder().goodsId(goods.getGoodsId())
                .goodsCode(goods.getGoodsCode()).goodsDesc(goods.getGoodsDesc()).goodsName(goods.getGoodsName())
                .iconImage(goods.getIconImage()).isDeleted(goods.getIsDeleted()).isOnline(goods.getIsOnline()).build();
        return collectMsg;
    }

}
