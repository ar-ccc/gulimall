package com.arccc.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.coupon.entity.SeckillSessionEntity;

import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 12:07:59
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

