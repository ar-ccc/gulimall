package com.arccc.gulimall.order.dao;

import com.arccc.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 12:31:22
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
