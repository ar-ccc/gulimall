package com.arccc.gulimall.product.service;

import com.arccc.gulimall.product.vo.AttrGroupRelationVo;
import com.arccc.gulimall.product.vo.AttrResponseVo;
import com.arccc.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:04:22
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBasePage(Map<String, Object> params, Long id, String type);

    AttrResponseVo getInfo(Long attrId);

    void updataAttr(AttrVo attr);

    List<AttrEntity> getRelation(Long id);

    void deleteAttrGroupRelations(AttrGroupRelationVo[] vos);

    PageUtils getNoRelation(Map<String, Object> params, Long attrService);

}

