package com.arccc.gulimall.product.service.impl;

import com.arccc.gulimall.product.entity.BrandEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;

import com.arccc.gulimall.product.dao.AttrGroupDao;
import com.arccc.gulimall.product.entity.AttrGroupEntity;
import com.arccc.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        if (catelogId==0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<AttrGroupEntity>());
            return new PageUtils(page);
        }else {
            String key = (String) params.get("key");
            QueryWrapper<AttrGroupEntity> brandEntityQueryWrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id",catelogId);
            if (StringUtils.isNotEmpty(key)){
                brandEntityQueryWrapper.and(obj->{
                    obj.eq("attr_group_id",key).or().like("attr_group_name ",key);
                });
            }
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    brandEntityQueryWrapper
            );
            return new PageUtils(page);

        }
    }

}