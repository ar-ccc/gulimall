package com.arccc.gulimall.order.to;

import com.arccc.gulimall.order.entity.OrderEntity;
import com.arccc.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class OrderCreateTo {
    private OrderEntity orderEntity;//订单信息
    private List<OrderItemEntity> items;//商品信息
    private BigDecimal payPrice;//总支付价格
    private BigDecimal fare;//运费
}
