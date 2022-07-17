package com.arccc.gulimall.order.vo;

import com.arccc.gulimall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitRespVO {
    private OrderEntity orderEntity;
    private Integer code;
}
