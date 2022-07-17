package com.arccc.gulimall.product.service;

import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.product.entity.CategoryEntity;
import com.arccc.gulimall.product.vo.Catelog2Vo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:04:22
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(Long[] catIds);

    /**
     *  找到 catelogId 的完整路径
     *  父/子
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    void updateDetail(CategoryEntity category);

    List<CategoryEntity> getLevel1Categorys();

    Map<String, List<Catelog2Vo>> getCatelogJson();
}

