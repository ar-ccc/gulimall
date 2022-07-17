package com.arccc.gulimall.ware.vo;

import lombok.Data;

@Data
public class LockStockResult {
    private Long skuId;//商品sku
    private Integer count;//锁定库存
    private Boolean locked;//结果
}
