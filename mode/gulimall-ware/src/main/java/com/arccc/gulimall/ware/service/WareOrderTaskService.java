package com.arccc.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 12:43:40
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

