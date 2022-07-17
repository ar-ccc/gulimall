package com.arccc.gulimall.product.service;

import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.product.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:04:22
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ProductAttrValueEntity> listforspu(Long spuId);

    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> attrs);
}

