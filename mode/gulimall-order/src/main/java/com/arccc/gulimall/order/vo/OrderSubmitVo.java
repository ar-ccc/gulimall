package com.arccc.gulimall.order.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 封装订单提交数据的vo
 */
@Data
@ToString
public class OrderSubmitVo {
    private Long addrId;//收货地址id
    private Integer payType = 1;//支付方式
    // 商品从购物车获取
    // 发票，积分。。。
    private String orderToken;//放重令牌
    //用户信息在session里面
}
