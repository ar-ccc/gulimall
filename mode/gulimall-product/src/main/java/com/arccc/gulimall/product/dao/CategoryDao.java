package com.arccc.gulimall.product.dao;

import com.arccc.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:04:22
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
