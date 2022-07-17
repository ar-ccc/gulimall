package com.arccc.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SpuBoundsVo {
    private Long id;
    private Long spuId;
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;
    private Integer work;
}
