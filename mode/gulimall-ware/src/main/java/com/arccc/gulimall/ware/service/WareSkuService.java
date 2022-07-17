package com.arccc.gulimall.ware.service;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.vo.mq.OrderTo;
import com.arccc.common.vo.mq.WareOrderTaskTo;
import com.arccc.gulimall.ware.entity.WareSkuEntity;
import com.arccc.gulimall.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 12:43:40
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    Map<Long, Boolean> hasStocks(List<Long> skuIds);

    Boolean lock(WareSkuLockVo vo);
    void unLock(WareOrderTaskTo wareOrderTaskTo);

    void unLock(OrderTo order);
}

