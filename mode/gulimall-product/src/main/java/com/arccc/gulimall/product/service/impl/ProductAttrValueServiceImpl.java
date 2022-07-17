package com.arccc.gulimall.product.service.impl;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;
import com.arccc.gulimall.product.dao.ProductAttrValueDao;
import com.arccc.gulimall.product.entity.ProductAttrValueEntity;
import com.arccc.gulimall.product.service.ProductAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ProductAttrValueEntity> listforspu(Long spuId) {

        return this.baseMapper.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
    }

    /**
     * 这是修改商品规格的方法
     * @param spuId
     * @param attrs
     */
    @Transactional
    @Override
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> attrs) {
        this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));
        List<ProductAttrValueEntity> collect = attrs.stream().map(item -> {
            item.setSpuId(spuId);
            item.setAttrSort(0);
            return item;
        }).collect(Collectors.toList());
        saveBatch(collect);
    }

}