package com.arccc.gulimall.order.service;

import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.order.entity.OrderEntity;
import com.arccc.gulimall.order.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 12:31:22
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 订单确认
     * @return 订单确认页需要返回的数据
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitRespVO placeAnOrder(OrderSubmitVo vo);

    void closeOrder(OrderEntity orderEntity);

    OrderEntity getOrderByOrderSn(String orderSn);

    PayVo getPayVoByOrderSn(String orderSn);

    PageUtils getOrderListByUserId(Map<String, Object> params);

    String handlePayResult(PayAsyncVo vo);
}

