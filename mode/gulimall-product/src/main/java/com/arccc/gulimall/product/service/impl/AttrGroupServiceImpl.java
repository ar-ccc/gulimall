package com.arccc.gulimall.product.service.impl;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;
import com.arccc.gulimall.product.dao.AttrGroupDao;
import com.arccc.gulimall.product.entity.AttrEntity;
import com.arccc.gulimall.product.entity.AttrGroupEntity;
import com.arccc.gulimall.product.service.AttrGroupService;
import com.arccc.gulimall.product.service.AttrService;
import com.arccc.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.arccc.gulimall.product.vo.SpuItemBaseAttrGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrService attrService;
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
        QueryWrapper<AttrGroupEntity> brandEntityQueryWrapper = new QueryWrapper<AttrGroupEntity>();
        String key = (String) params.get("key");


        if (catelogId!=0){
            brandEntityQueryWrapper.eq("catelog_id",catelogId);
        }
        if (StringUtils.isNotEmpty(key)){
            brandEntityQueryWrapper.and(obj->{
                obj.eq("attr_group_id",key).or().like("attr_group_name ",key);
            });
        }
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                brandEntityQueryWrapper);
        return new PageUtils(page);
    }

    /**
     *  根据分类ID查询所有分组和分组属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        // 1、查询分类下的所有分组
        List<AttrGroupEntity> catelog_id = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 2、根据分组查询所有属性
        List<AttrGroupWithAttrsVo> vos = catelog_id.stream().map(item -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item,attrGroupWithAttrsVo);
            // 通过分组id获取分组属性
            List<AttrEntity> relation = attrService.getRelation(item.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(relation);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());

        return vos;
    }

    /**
     * 查出当前spu对应的所有属性分组信息，以及当前属性对应的值
     *
     * @param spuId
     * @param catalogId
     * @return
     */
    @Override
    public List<SpuItemBaseAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        return baseMapper.getAttrGroupWithAttrsBySpuId(spuId,catalogId);

    }

}