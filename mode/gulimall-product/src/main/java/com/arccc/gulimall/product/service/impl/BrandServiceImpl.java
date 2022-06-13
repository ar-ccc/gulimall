package com.arccc.gulimall.product.service.impl;

import com.arccc.gulimall.product.dao.CategoryBrandRelationDao;
import com.arccc.gulimall.product.entity.CategoryBrandRelationEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;

import com.arccc.gulimall.product.dao.BrandDao;
import com.arccc.gulimall.product.entity.BrandEntity;
import com.arccc.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    CategoryBrandRelationDao categoryBrandRelationDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 获取key
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        // 模糊查询
        if (StringUtils.isNotEmpty(key)){
            queryWrapper.eq("brand_id",key).or().like("name",key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void updateDetail(BrandEntity brand) {
        baseMapper.updateById(brand);
        // 保证数据一致性
        if(StringUtils.isNotEmpty(brand.getName())){
            CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
//            categoryBrandRelationEntity.setBrandId(brand.getBrandId());
            categoryBrandRelationEntity.setBrandName(brand.getName());

            categoryBrandRelationDao.update(categoryBrandRelationEntity,new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brand.getBrandId()));

            // TODO:更新其他品牌关联信息
        }
    }

}