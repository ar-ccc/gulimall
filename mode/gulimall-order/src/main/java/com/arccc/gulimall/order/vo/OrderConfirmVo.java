package com.arccc.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认页面数据
 */
@Data
public class OrderConfirmVo {
    //用户收货地址列列表
    List<MemberAddressVo> address;
    // 所有选中的购物项
    List<OrderItemVo> items;

    //发票。。其他

    //积分
    private Integer integration;
    //防重令牌
    private String orderToken;
    //商品数量
    public Integer getCount(){
        if (items != null){
            int i =0;
            for (OrderItemVo item : items) {
                i+=item.getCount();
            }
            return i;
        }
        return 0;
    }

    //订单总额
//    private BigDecimal total;
    public BigDecimal getTotal(){
        return getBigDecimal();
    }

    private BigDecimal getBigDecimal() {
        BigDecimal decimal = new BigDecimal("0");
        if (items != null){
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount()));
                decimal = decimal.add(multiply);
            }
        }
        return decimal;
    }

    //应付总额
//    private BigDecimal payPrice;
    public BigDecimal getPayPrice(){
        return getBigDecimal();
    }
}
