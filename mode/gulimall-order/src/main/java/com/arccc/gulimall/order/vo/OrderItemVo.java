package com.arccc.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class OrderItemVo {
    private Long skuId;//商品id
    private String title;//标题
    private Boolean check;//选中状态
    private String img;//图片
    private List<String> skuSaleAttr;//销售属性
    private BigDecimal price;//单价
    private Integer count;//数量
    private BigDecimal totalPrice;//总价
    //todo 查询库存状态
    private Boolean hasStock=false;//是否有货
}
