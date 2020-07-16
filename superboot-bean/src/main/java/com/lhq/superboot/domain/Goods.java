package com.lhq.superboot.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 商品暂定
 * @author: lihaoqi
 * @date: 2019年4月25日
 * @version: 1.0.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Goods {

    private String goodsId;

    private String goodsInfoId;

    private String goodsTypeId;

    private Integer goodsCategories;

    private Integer sort;

    private String goodsCode;

    private String goodsName;

    private String goodsDesc;

    private String companyId;

    private String supplierId;

    private String titleImage;

    private String iconImage;

    private String bannerImage;

    private String infoImage;

    private Integer isGroup;

    private Integer isOnline;

    private Integer isDeleted;

    private Date createTime;

    private String createUser;

    private Date modifyTime;

    private String modifyUser;

}