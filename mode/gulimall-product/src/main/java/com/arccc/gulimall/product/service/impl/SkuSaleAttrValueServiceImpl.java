package com.arccc.gulimall.product.service.impl;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;
import com.arccc.gulimall.product.dao.SkuSaleAttrValueDao;
import com.arccc.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.arccc.gulimall.product.service.SkuSaleAttrValueService;
import com.arccc.gulimall.product.vo.SkuItemSaleAttrVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId) {
        return  baseMapper.getSaleAttrsBySpuId(spuId);
    }

    @Override
    public List<String> getSkuSale(Long id) {
        return baseMapper.getSaleBySkuId(id);
    }

}