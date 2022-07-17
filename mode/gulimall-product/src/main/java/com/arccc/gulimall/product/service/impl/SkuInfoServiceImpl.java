package com.arccc.gulimall.product.service.impl;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;
import com.arccc.common.vo.OrderItemProductRespVo;
import com.arccc.gulimall.product.dao.BrandDao;
import com.arccc.gulimall.product.dao.SkuInfoDao;
import com.arccc.gulimall.product.dao.SkuSaleAttrValueDao;
import com.arccc.gulimall.product.dao.SpuInfoDao;
import com.arccc.gulimall.product.entity.*;
import com.arccc.gulimall.product.service.*;
import com.arccc.gulimall.product.vo.SkuItemSaleAttrVo;
import com.arccc.gulimall.product.vo.SkuItemVo;
import com.arccc.gulimall.product.vo.SpuItemBaseAttrGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    SpuInfoDao spuInfoDao;
    @Autowired
    BrandDao brandDao;
    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        /**
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String) params.get("key");
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        String  min = (String) params.get("min");
        String max = (String) params.get("max");
        if (StringUtils.isNotEmpty(key)){
            wrapper.and(obj ->{
                obj.eq("sku_id",key).or().like("sku_name",key);
            });
        }
        if (StringUtils.isNotEmpty(catelogId) && Integer.parseInt(catelogId)!=0){
            wrapper.eq("catalog_id",catelogId);
        }
        if (StringUtils.isNotEmpty(brandId) && Integer.parseInt(brandId)!= 0){
            wrapper.eq("brand_id",brandId);
        }
        if (StringUtils.isNotEmpty(min) ){
            BigDecimal bigDecimal = new BigDecimal(min);
            if (bigDecimal.compareTo(new BigDecimal(0))>0){
                wrapper.ge("price",min);
            }
        }
        if (StringUtils.isNotEmpty(max) ){
            BigDecimal bigDecimal = new BigDecimal(max);
            if (bigDecimal.compareTo(new BigDecimal(0))>0){
                wrapper.le("price",max);
            }
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuByCatelogIdAndKeyword(Long catelog3Id,String keyword) {


        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        if (catelog3Id != null){
            wrapper.eq("catalog_id",catelog3Id);
        }
        if (StringUtils.isNotEmpty(keyword)){
            wrapper.and(obj->{
                obj.eq("sku_name",keyword).or().like("sku_title",keyword).or().like("sku_subtitle",keyword);
            });
        }
        return baseMapper.selectList(wrapper);
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo vo = new SkuItemVo();
        //1、sku基本信息 `pms_sku_info`
        CompletableFuture<SkuInfoEntity> info = CompletableFuture.supplyAsync(() -> getById(skuId), threadPoolExecutor);
        CompletableFuture.runAsync(()->{
            //2、sku图片信息 `pms_sku_images`
            List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
            vo.setSkuImagesEntities(images);
        },threadPoolExecutor);
        vo.setInfo(info.get());
        CompletableFuture<Void> saleAttr = info.thenAcceptAsync(result -> {
            //3、spu销售属性组合 `pms_spu_info`
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(result.getSpuId());
            vo.setSaleAttr(saleAttrVos);
        }, threadPoolExecutor);

        CompletableFuture<Void> decp = info.thenAcceptAsync(result -> {
            //4、spu介绍
            SpuInfoDescEntity spuInfo = spuInfoDescService.getById(result.getSpuId());
            vo.setSpuInfoDescEntity(spuInfo);
        }, threadPoolExecutor);

        CompletableFuture<Void> group = info.thenAcceptAsync(result -> {
            //5、spu规格属性
            List<SpuItemBaseAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(result.getSpuId(), result.getCatalogId());
            vo.setGroupAttrs(attrGroupVos);
        }, threadPoolExecutor);

        CompletableFuture.allOf(saleAttr,decp,group).get();


/*
        //1、sku基本信息 `pms_sku_info`
        SkuInfoEntity byId = getById(skuId);
        Long spuId = byId.getSpuId();
        Long catalogId = byId.getCatalogId();
        vo.setInfo(byId);
        //2、sku图片信息 `pms_sku_images`
        List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
        vo.setSkuImagesEntities(images);
        //3、spu销售属性组合 `pms_spu_info`
        List<SkuItemSaleAttrVo>  saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
        vo.setSaleAttr(saleAttrVos);
        //4、spu介绍
        SpuInfoDescEntity spuInfo = spuInfoDescService.getById(spuId);
        vo.setSpuInfoDescEntity(spuInfo);
        //5、spu规格属性
        List<SpuItemBaseAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId,catalogId);
        vo.setGroupAttrs(attrGroupVos);

 */
        return vo;
    }


    /**
     * 查询订单模块需要的信息
     * @param skuIds
     * @return
     */
    @Override
    public List<OrderItemProductRespVo> getOrderItemBySkuIds(List<Long> skuIds) {
        // 1、获得sku相关信息
        List<SkuInfoEntity> skuInfoEntities = baseMapper.selectBatchIds(skuIds);
        List<OrderItemProductRespVo> orderItemProductRespVos = skuInfoEntities.stream().map(item -> {
            OrderItemProductRespVo vo = new OrderItemProductRespVo();
            vo.setSkuId(item.getSkuId());
            vo.setSkuName(item.getSkuName());
            vo.setSkuPrice(item.getPrice());
            vo.setSpuId(item.getSpuId());
            vo.setSpuBrand(item.getBrandId()+"");
            vo.setCategoryId(item.getCatalogId());
            vo.setSkuPic(item.getSkuDefaultImg());
            return vo;
        }).collect(Collectors.toList());
        // 2、获取并设置spu名称
        Set<Long> spuIds = skuInfoEntities.stream().map(SkuInfoEntity::getSpuId).collect(Collectors.toSet());
        List<SpuInfoEntity> spuInfoEntities = spuInfoDao.selectBatchIds(spuIds);
        Map<Long, String> spuIdAndSpuName = spuInfoEntities.stream().collect(Collectors.toMap(SpuInfoEntity::getId, SpuInfoEntity::getSpuName));
        orderItemProductRespVos =orderItemProductRespVos.stream().peek(item -> item.setSpuName(spuIdAndSpuName.get(item.getSpuId()))).collect(Collectors.toList());
        // 3、获取并设置品牌名称
        Set<Long> brandIds = skuInfoEntities.stream().map(SkuInfoEntity::getBrandId).collect(Collectors.toSet());
        List<BrandEntity> brandEntities = brandDao.selectBatchIds(brandIds);
        Map<Long, String> brandIdAndBrandName = brandEntities.stream().collect(Collectors.toMap(BrandEntity::getBrandId, BrandEntity::getName));
        orderItemProductRespVos = orderItemProductRespVos.stream().peek(item -> item.setSpuBrand(brandIdAndBrandName.get(Long.parseLong(item.getSpuBrand())))).collect(Collectors.toList());
        // 4、设置销售属性
        List<String> addrJson = skuSaleAttrValueDao.getSkuIdAndSkuSaleAttrJson(skuIds);
        Map<Long, String> addrJsonMap = addrJson.stream().map(item -> item.split("_")).collect(Collectors.toMap(key -> Long.parseLong(key[0]), v -> v[1]));
        orderItemProductRespVos = orderItemProductRespVos.stream().peek(item -> item.setSkuAttrsVals(addrJsonMap.get(item.getSkuId()))).collect(Collectors.toList());
        return orderItemProductRespVos;
    }


}