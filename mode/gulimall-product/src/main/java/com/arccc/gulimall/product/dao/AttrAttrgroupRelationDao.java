package com.arccc.gulimall.product.dao;

import com.arccc.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:04:22
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatchRelation(@Param("entitys") List<AttrAttrgroupRelationEntity> entitys);

}
