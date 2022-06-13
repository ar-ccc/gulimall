package com.arccc.gulimall.product.service.impl;

import com.arccc.gulimall.product.dao.BrandDao;
import com.arccc.gulimall.product.dao.CategoryDao;
import com.arccc.gulimall.product.entity.BrandEntity;
import com.arccc.gulimall.product.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;

import com.arccc.gulimall.product.dao.CategoryBrandRelationDao;
import com.arccc.gulimall.product.entity.CategoryBrandRelationEntity;
import com.arccc.gulimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {
    @Autowired
    BrandDao brandDao;
    @Autowired
    CategoryDao categoryDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        BrandEntity brandEntity = brandDao.selectById(brandId);
        Long catelogId = categoryBrandRelation.getCatelogId();
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        this.save(categoryBrandRelation);
    }

}