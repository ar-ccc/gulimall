package com.arccc.gulimall.ware.service;

import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.ware.entity.PurchaseEntity;
import com.arccc.gulimall.ware.service.vo.MergeVo;
import com.arccc.gulimall.ware.service.vo.PurchaseDoneVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 12:43:40
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void received(List<Long> ids);

    void done(PurchaseDoneVo purchaseDoneVo);
}

