package com.arccc.common.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装创建订单项
 * 商品系统需要返回的数据
 */
@Data
public class OrderItemProductRespVo {
    /**
     * spu_id
     */
    private Long spuId;
    /**
     * spu_name
     */
    private String spuName;
    /**
     * spu_pic
     */
    private String spuPic;
    /**
     * 品牌
     */
    private String spuBrand;
    /**
     * 商品分类id
     */
    private Long categoryId;
    /**
     * 商品sku编号
     */
    private Long skuId;
    /**
     * 商品sku名字
     */
    private String skuName;
    /**
     * 商品sku图片
     */
    private String skuPic;
    /**
     * 商品sku价格
     */
    private BigDecimal skuPrice;
    /**
     * 商品销售属性组合（JSON）
     */
    private String skuAttrsVals;
}
