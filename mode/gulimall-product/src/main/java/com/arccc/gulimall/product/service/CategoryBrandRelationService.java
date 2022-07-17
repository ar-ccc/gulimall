package com.arccc.gulimall.product.service;

import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.product.entity.BrandEntity;
import com.arccc.gulimall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:04:22
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    List<BrandEntity> getBrandsByCatId(Long catId);
}

