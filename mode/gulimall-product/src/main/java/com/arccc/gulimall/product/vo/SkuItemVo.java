package com.arccc.gulimall.product.vo;

import com.arccc.gulimall.product.entity.SkuImagesEntity;
import com.arccc.gulimall.product.entity.SkuInfoEntity;
import com.arccc.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString
public class SkuItemVo {
    //1、sku基本信息 `pms_sku_info`
    SkuInfoEntity info;
    //2、sku图片信息 `pms_sku_images`
    List<SkuImagesEntity> skuImagesEntities;
    //3、spu销售属性组合 `pms_spu_info`
    List<SkuItemSaleAttrVo> saleAttr;
    //4、spu介绍
    SpuInfoDescEntity spuInfoDescEntity;
    //5、spu规格属性
    List<SpuItemBaseAttrGroupVo> groupAttrs;
    //6、是否有货
    boolean hasStock =true;


}
