package com.arccc.gulimall.product.service.impl;

import com.arccc.common.constant.ProductConstant;
import com.arccc.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.arccc.gulimall.product.dao.AttrGroupDao;
import com.arccc.gulimall.product.dao.CategoryDao;
import com.arccc.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.arccc.gulimall.product.entity.AttrGroupEntity;
import com.arccc.gulimall.product.entity.CategoryEntity;
import com.arccc.gulimall.product.service.CategoryService;
import com.arccc.gulimall.product.vo.AttrGroupRelationVo;
import com.arccc.gulimall.product.vo.AttrResponseVo;
import com.arccc.gulimall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;

import com.arccc.gulimall.product.dao.AttrDao;
import com.arccc.gulimall.product.entity.AttrEntity;
import com.arccc.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    CategoryDao categoryDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryService categoryService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        //org.springframework.beans.BeanUtils;
        // 用于封装参数名类型相同的bean数据
        BeanUtils.copyProperties(attr,attrEntity);
        // 1、保存基本数据
        this.save(attrEntity);
        if (attr.getAttrType()== ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null){
            // 2、保存关联关系
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }

    /**
     * @param params 包括分页参数和关键字key的集合
     * @param id     查询的三级分类id
     * @param type
     * @return
     */
    @Override
    public PageUtils queryBasePage(Map<String, Object> params, Long id, String type) {
        QueryWrapper<AttrEntity> attrEntityQueryWrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type","base".equalsIgnoreCase(type)
                        ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                        : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        // 设置分类查询
        if (id != 0){
            attrEntityQueryWrapper.eq("catelog_id",id);
        }
        // 设置关键字查询
        String  key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)){
            attrEntityQueryWrapper.and(obj ->{
                obj.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        // 查询数据
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), attrEntityQueryWrapper);

        // key为分类id，value为分类name
        Map<Long ,String > catelogNames = new HashMap<>();

        // 获取分页查询的全部数据
        List<AttrEntity> records = page.getRecords();
        // 如果没有分类id，就从分页数据中获取分类id并查询出所有分类名称
        if (id == 0){
            // 1、创建保存id的集合
            Set<Long> catids = new HashSet<>();
            // 2、获取所有id
            records.forEach(r ->  catids.add(r.getCatelogId()));
            // 3、发送请求获取含有id的分类
            List<CategoryEntity> categoryEntities = categoryDao.selectBatchIds(catids);
            // 4、将全部数据保存到catelogNames中
            categoryEntities.forEach(r -> {
                if (r != null){
                    catelogNames.put(r.getCatId(),r.getName());
                }
            });
        }else {

            // 如果有分类id则通过分类id将分类name查询出来
            CategoryEntity categoryEntity = categoryDao.selectById(id);
            if (categoryEntity != null)
                catelogNames.put(id,categoryEntity.getName());
        }


        // key为属性id，value为分组实体
        Map<Long,AttrGroupEntity > attrGroupNames = new HashMap<>();
        if ("base".equalsIgnoreCase(type)){
            // 获取所有属性id和对应的分组name;
            // 1、获取全部属性id
            List<Long> attrids = new ArrayList<>();
            records.forEach(r->attrids.add(r.getAttrId()));
            // 2、根据属性id查询出全部属性对应的分组id
            List<AttrAttrgroupRelationEntity> attrgroupRelationEntities = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_id", attrids));
            // key为attrid ，value为groupid
            Map<Long,Long> map = new HashMap<>();
            attrgroupRelationEntities.forEach(r -> map.put(r.getAttrId(),r.getAttrGroupId()) );
            // 获取全部groupid
            Set<Long> groupids = new HashSet<>();
            attrgroupRelationEntities.forEach( r -> groupids.add(r.getAttrGroupId()));
            // 3、根据分组id查询出所有分组实体
            List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectBatchIds(groupids);

            // 4、保存属性id和分组name
            for (Long groupid : attrids) {
                for (AttrGroupEntity groupEntity : attrGroupEntities) {
                    Long gid = map.get(groupid);
                    Long agid = groupEntity.getAttrGroupId();
                    if (gid != null && gid.equals(agid)){
                        attrGroupNames.put(groupid,groupEntity);
                        break;
                    }
                }

            }
        }

        // 将查询到的数据封装到AttrResponseVo中，并获取属性分组名和三级分类名称
        Stream<AttrEntity> stream = records.stream();
        List<AttrResponseVo> collect = stream.map(attrEntity -> {
            // 1、创建vo对象
            AttrResponseVo attrResponseVo = new AttrResponseVo();
            // 2、将数据保存到vo对象中
            BeanUtils.copyProperties(attrEntity, attrResponseVo);
            // 3、将分类名和分组名保存到vo中
            attrResponseVo.setCatelogName(catelogNames.get(attrEntity.getCatelogId()));

            AttrGroupEntity attrGroupEntity = attrGroupNames.get(attrEntity.getAttrId());
            if (attrGroupEntity != null){
                attrResponseVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
            // 4、返回
            return attrResponseVo;
        }).collect(Collectors.toList());

        PageUtils pageUtils = new PageUtils(page);
        // 将重新封装完成的数据设置回分页中
        pageUtils.setList(collect);
        return pageUtils;
    }


    @Override
    public AttrResponseVo getInfo(Long attrId) {
        AttrEntity byId = this.getById(attrId);
        AttrResponseVo attrResponseVo = new AttrResponseVo();
        BeanUtils.copyProperties(byId,attrResponseVo);
        if (attrResponseVo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            // 1、设置分组
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (attrAttrgroupRelationEntity != null){
                // 分组id
                attrResponseVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity != null){
                    // 分组名
                    attrResponseVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }

            }
        }

        // 2、设置分类信息
        Long catelogId = attrResponseVo.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrResponseVo.setCatelogPath(catelogPath);
        CategoryEntity byId1 = categoryService.getById(catelogId);
        if (byId1 != null){
            attrResponseVo.setAttrName(byId1.getName());
        }


        return attrResponseVo;
    }

    /**
     *
     * @param attr
     */
    @Transactional
    @Override
    public void updataAttr(AttrVo attr) {
        // 基础修改
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);

        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId()!= null){
            Integer count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (count > 0){
                // 修改分组关联
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
                attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
                attrAttrgroupRelationDao.update(attrAttrgroupRelationEntity, new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
            }else {
                // 新增分组关联
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
                attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
                attrAttrgroupRelationEntity.setAttrSort(0);
                attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }



    }

    /**
     *  根据分组id找到分组关联的所有属性
     * @param id
     * @return
     */
    @Override
    public List<AttrEntity> getRelation(Long id) {
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntities = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", id));
        List<Long> collect = attrgroupRelationEntities.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());
        if (collect == null || collect.size()==0){
            return null;
        }
        Collection<AttrEntity> entities = this.listByIds(collect);
        return (List<AttrEntity>) entities;
    }

    @Override
    public void deleteAttrGroupRelations(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> collect = Arrays.asList(vos).stream().map(item -> {
            AttrAttrgroupRelationEntity entitys = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, entitys);
            return entitys;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBatchRelation(collect);
    }

    /**
     *  获取同分类下，当前分组没有关联的所有属性分页
     * @param params
     * @param attrGroupId
     * @return
     */
    @Override
    public PageUtils getNoRelation(Map<String, Object> params, Long attrGroupId) {
        //1、当前分组只能关联所属分类下的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
        CategoryEntity categoryEntity = categoryDao.selectById(attrGroupEntity.getCatelogId());
        //2、只能关联别的分组没有引用的数据
        //2.1、找到当前分类下的所有属性
        //2.1.1、设置查询条件
        QueryWrapper<AttrEntity> attr_type = new QueryWrapper<AttrEntity>().eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        attr_type.eq("catelog_id",categoryEntity.getCatId());
        //2.1.2、通过分页进行查询
//        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),attr_type);
        //2.1.3、获取当前分类下的所有属性
        List<AttrEntity> records = baseMapper.selectList(attr_type);
        //2.2、找到当前分类下被分组引用的属性
        //2.2.1、获取全部属性id
        List<Long> collect = records.stream().map(AttrEntity::getAttrId).collect(Collectors.toList());
        //2.2.2、 将全部属性id去关联表查询，将返回的关联对象的id全部取出来
        if ( collect.size() != 0){
            List<Long> noIds = attrAttrgroupRelationDao.selectList(
                            new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_id", collect))
                    .stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            //2.2.3、 将所有被关联的属性查出来
            List<AttrEntity> nolist = baseMapper.selectBatchIds(noIds);
            //2.3、剔除被引用的属性
            if (nolist != null && nolist.size() != 0){
                records.removeAll(nolist);
            }
        }

        IPage<AttrEntity> page = null;
        List<Long> longList = records.stream().map(AttrEntity::getAttrId).collect(Collectors.toList());
        if ( longList.size() != 0){
            String  key = (String) params.get("key");
            if ( key != null){
                attr_type.and( i -> {
                    i.eq("attr_id",key).or().like("attr_name",key);
                });
            }
            attr_type.in("attr_id",longList);

            page = this.page(new Query<AttrEntity>().getPage(params), attr_type);
        }
        assert page != null;
        return  new PageUtils(page);
    }


}