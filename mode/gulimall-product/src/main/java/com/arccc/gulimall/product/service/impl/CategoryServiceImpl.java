package com.arccc.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;

import com.arccc.gulimall.product.dao.CategoryDao;
import com.arccc.gulimall.product.entity.CategoryEntity;
import com.arccc.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查出所有分类，并把子分类放到父分类下
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有分类
        List<CategoryEntity> categoryEntities = this.list();
        //把子分类放到父分类下
        //找出所有一级分类
        List<CategoryEntity> level1 = categoryEntities.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(categoryEntity -> {
                    //把一级分类的子分类放到一级分类下
                    categoryEntity.setChildCatList(getChildren(categoryEntity,categoryEntities));
                    return categoryEntity;
                })
                //按照sort排序，小的在前，大的在后
                .sorted(Comparator.comparing(CategoryEntity::getSort))
                .collect(Collectors.toList());

        return level1;
    }

    /**
     *
     * @param catIds
     */
    @Override
    public void removeMenuByIds(Long[] catIds) {
        //TODO 1、检查菜单分类是否被使用

        // 2、删除菜单分类
        baseMapper.deleteBatchIds(Arrays.asList(catIds));
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        findParentPath(catelogId,path);
        return path.toArray(new Long[path.size()]);
    }
    private List<Long> findParentPath(Long catelogId , List<Long> path){
        CategoryEntity categoryEntity = baseMapper.selectById(catelogId);
        if (categoryEntity.getParentCid() != 0){
            findParentPath(categoryEntity.getParentCid(),path);
        }
        path.add(catelogId);
        return path;
    }

    //根据父分类id查询子分类,并按照sort排序,小的在前，大的在后
    public List<CategoryEntity> getChildren(CategoryEntity categoryEntity,List<CategoryEntity> all){
        List<CategoryEntity> childCatList = all.stream()
                .filter(categoryEntity1 -> categoryEntity1.getParentCid().equals(categoryEntity.getCatId()))
                .map(categoryEntity1 -> {
                    categoryEntity1.setChildCatList(getChildren(categoryEntity1,all));
                    return categoryEntity1;
                })
                .sorted(Comparator.comparing(CategoryEntity::getSort))
                .collect(Collectors.toList());
        if (childCatList.size()==0){
            return null;
        }
        return childCatList;
    }
}