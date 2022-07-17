package com.arccc.gulimall.product.service;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.vo.OrderItemProductRespVo;
import com.arccc.gulimall.product.entity.SkuInfoEntity;
import com.arccc.gulimall.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:04:22
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkuByCatelogIdAndKeyword(Long catelog3Id,String keyword);

    SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException;

    List<OrderItemProductRespVo> getOrderItemBySkuIds(List<Long> skuIds);
}

