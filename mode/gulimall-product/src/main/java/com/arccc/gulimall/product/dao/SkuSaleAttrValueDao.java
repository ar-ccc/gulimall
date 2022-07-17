package com.arccc.gulimall.product.dao;

import com.arccc.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.arccc.gulimall.product.vo.SkuItemSaleAttrVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:04:22
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(@Param("spuId") Long spuId);

    List<String> getSaleBySkuId(Long id);

    List<String> getSkuIdAndSkuSaleAttrJson(@Param("skuIds")List<Long> skuIds);
}
