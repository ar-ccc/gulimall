package com.arccc.gulimall.product.service.impl;

import com.arccc.common.to.SkuReductionTo;
import com.arccc.common.to.SpuBoundsTo;
import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;
import com.arccc.common.utils.R;
import com.arccc.gulimall.product.dao.SpuInfoDao;
import com.arccc.gulimall.product.entity.*;
import com.arccc.gulimall.product.feign.CoupenFeignService;
import com.arccc.gulimall.product.service.*;
import com.arccc.gulimall.product.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService  skuSaleAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    CoupenFeignService coupenFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 这是新增spu商品方法
     * @param vo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        // 1、保存spu基本信息 //`pms_spu_info`
        Long spuInfoId = this.SaveSpuInfo(vo);

        // 2、保存spu的描述图片 //`pms_spu_info_desc`
        List<String> decript = vo.getDecript();
        if (decript != null && decript.size() != 0){
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuInfoId);
            spuInfoDescEntity.setDecript(String.join(",",decript));
            spuInfoDescService.saveSpuinfoDesc(spuInfoDescEntity);
        }


        // 3、保存spu图片集 //`pms_spu_images`
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoId,images);

        // 4、保存spu规格参数 //`pms_product_attr_value`
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        if (baseAttrs != null && baseAttrs.size() != 0){
            List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(attr -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                AttrEntity byId = attrService.getById(attr.getAttrId());

                productAttrValueEntity.setAttrName(byId.getAttrName());
                productAttrValueEntity.setAttrId(attr.getAttrId());
                productAttrValueEntity.setSpuId(spuInfoId);
                productAttrValueEntity.setAttrSort(0);
                productAttrValueEntity.setAttrValue(attr.getAttrValues());
                productAttrValueEntity.setQuickShow(attr.getShowDesc());
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            productAttrValueService.saveBatch(productAttrValueEntities);
        }

        // 5、保存spu积分信息
        Bounds bounds = vo.getBounds();
        if (bounds != null){
            SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
            BeanUtils.copyProperties(bounds,spuBoundsTo);
            spuBoundsTo.setSpuId(spuInfoId);
            R r = coupenFeignService.saveSpuBounds(spuBoundsTo);

            log.info("spu积分信息保存结果{}",r.get("code"));
        }


        // 6、保存spu对应的所有sku信息
        List<Skus> skus = vo.getSkus();
        Long brandId = vo.getBrandId();
        Long catalogId = vo.getCatalogId();
        if (skus != null && skus.size() != 0){
            skus.forEach(item ->{
                // 6.1、sku基本信息 //`pms_sku_info`
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                String defaultImg = "";
                List<Images> skuImages = item.getImages();
                if (skuImages != null && skuImages.size() != 0){
                    for (Images image : skuImages) {
                        if (image.getDefaultImg() == 1){
                            defaultImg=image.getImgUrl();
                        }
                    }
                }
                skuInfoEntity.setSpuId(spuInfoId);
                skuInfoEntity.setCatalogId(catalogId);
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoEntity.setBrandId(brandId);
                skuInfoEntity.setSaleCount(0L);
                skuInfoService.save(skuInfoEntity);

                // 6.2、sku图片信息 //`pms_sku_images`
                if (skuImages != null && skuImages.size() != 0){
                    List<SkuImagesEntity> collect = skuImages.stream()
                            //剔除imagesURL为空的
                            .filter(img->StringUtils.isNotEmpty(img.getImgUrl()))
                            .map(img -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        BeanUtils.copyProperties(img,skuImagesEntity);
                        skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                        skuImagesEntity.setImgSort(0);
                        return skuImagesEntity;
                    }).collect(Collectors.toList());

                    skuImagesService.saveBatch(collect);
                }

                // 6.3、sku销售属性 //`pms_sku_sale_attr_value`
                List<Attr> attrs = item.getAttr();
                if (attrs != null && attrs.size() != 0){
                    List<SkuSaleAttrValueEntity> collect = attrs.stream().map(attr -> {
                        SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                        BeanUtils.copyProperties(attr,skuSaleAttrValueEntity);
                        skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                        skuSaleAttrValueEntity.setAttrSort(0);
                        return skuSaleAttrValueEntity;
                    }).collect(Collectors.toList());
                    skuSaleAttrValueService.saveBatch(collect);
                }
                // 6.4、sku优惠满减信息
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuInfoEntity.getSkuId());
                if(skuReductionTo.getCountStatus() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) > 0) {
                    R r = coupenFeignService.savSkuReduction(skuReductionTo);
                    log.info("sku优惠满减信息保存结果{}", r.get("code"));
                }
            });
        }
    }

    /**
     *  保存spu信息
     * @param vo
     * @return 返回带创建好id的SpuInfoEntity
     */
    public Long SaveSpuInfo(SpuSaveVo vo) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        baseMapper.insert(spuInfoEntity);
        return spuInfoEntity.getId();
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> spuInfoEntityQueryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotEmpty(key)){
            spuInfoEntityQueryWrapper.and(obj ->{
                obj.eq("id",key).or().like("spu_name",key);
            });
        }
        if (StringUtils.isNotEmpty(status)){
            spuInfoEntityQueryWrapper.eq("publish_status",status);
        }
        if (StringUtils.isNotEmpty(catelogId) && Integer.parseInt(catelogId) != 0){
            spuInfoEntityQueryWrapper.eq("catalog_id",catelogId);
        }
        if (StringUtils.isNotEmpty(brandId) && Integer.parseInt(brandId)!=0){
            spuInfoEntityQueryWrapper.eq("brand_id",brandId);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                spuInfoEntityQueryWrapper
        );
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void up(Long spuId) {
        //TODO 通过ES完成商品上架 130P
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        spuInfoEntity.setId(spuId);
        spuInfoEntity.setUpdateTime(new Date());
        // TODO 枚举商品状态
        spuInfoEntity.setPublishStatus(1);

        baseMapper.updateById(spuInfoEntity);

    }



}