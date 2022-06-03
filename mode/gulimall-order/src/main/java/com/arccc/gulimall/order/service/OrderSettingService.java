package com.arccc.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.order.entity.OrderSettingEntity;

import java.util.Map;

/**
 * 订单配置信息
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 12:31:22
 */
public interface OrderSettingService extends IService<OrderSettingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

