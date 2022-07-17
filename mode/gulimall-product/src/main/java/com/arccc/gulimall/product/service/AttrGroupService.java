package com.arccc.gulimall.product.service;

import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.product.entity.AttrGroupEntity;
import com.arccc.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.arccc.gulimall.product.vo.SpuItemBaseAttrGroupVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:04:22
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    List<SpuItemBaseAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

