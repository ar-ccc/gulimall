package com.arccc.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.order.entity.OrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 12:31:22
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

