package com.arccc.gulimall.product.service.impl;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;
import com.arccc.gulimall.product.dao.CategoryBrandRelationDao;
import com.arccc.gulimall.product.dao.CategoryDao;
import com.arccc.gulimall.product.entity.CategoryBrandRelationEntity;
import com.arccc.gulimall.product.entity.CategoryEntity;
import com.arccc.gulimall.product.service.CategoryService;
import com.arccc.gulimall.product.vo.Catelog2Vo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    CategoryBrandRelationDao categoryBrandRelationDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查出所有分类，并把子分类放到父分类下
     * @return
     */

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有分类
        List<CategoryEntity> categoryEntities = this.list();
        //把子分类放到父分类下
        //找出所有一级分类
        List<CategoryEntity> level1 = categoryEntities.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(categoryEntity -> {
                    //把一级分类的子分类放到一级分类下
                    categoryEntity.setChildren(getChildren(categoryEntity,categoryEntities));
                    return categoryEntity;
                })
                //按照sort排序，小的在前，大的在后
                .sorted(Comparator.comparing(CategoryEntity::getSort))
                .collect(Collectors.toList());

        return level1;
    }

    /**
     *
     * @param catIds
     */
    @Override
    public void removeMenuByIds(Long[] catIds) {
        //TODO 1、检查菜单分类是否被使用

        // 2、删除菜单分类
        baseMapper.deleteBatchIds(Arrays.asList(catIds));
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        findParentPath(catelogId, path);
        return path.toArray(new Long[path.size()]);
    }

    /**
     * 执行方法后需要删除缓存
     *  1、可以使用@CacheEvict(value = {"category"},key = "'getLevel1Categorys'")对指定分区，指定key的缓存名称进行删除
     * 删除多个缓存
     *  1、使用@Caching()，@Caching可以执行多条语句
     *      @Caching(evict = {
     *             @CacheEvict(value = {"category"},key = "'getLevel1Categorys'"),
     *             @CacheEvict(value = {"category"},key = "'getCatelogJson'")
     *      })
     *  2、@CacheEvict(value = {"category"},allEntries = true),删除该分区下的所有key
     *  约定：缓存同一类型的数据时，指定同一分区名，在该类型更新时，同时清除该类型的所有缓存数据
     */
    @Caching(evict = {
            @CacheEvict(value = {"category"},key = "'getLevel1Categorys'"),
            @CacheEvict(value = {"category"},key = "'getCatelogJson'")
    })
    //@CacheEvict(value = {"category"},allEntries = true)
    @Transactional
    @Override
    public void updateDetail(CategoryEntity category) {
        baseMapper.updateById(category);
        if (StringUtils.isNotEmpty(category.getName())){
            CategoryBrandRelationEntity entity = new CategoryBrandRelationEntity();
            entity.setCatelogName(category.getName());
            categoryBrandRelationDao.update(entity,new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id",category.getCatId()));
        }

    }

    @Cacheable(value = {"category"},key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("查询一级数据库");
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    @Cacheable(value = {"category"},key = "#root.methodName",sync = true) //当前方法返回结果需要缓存，如果缓存中有缓存，则不在调用此方法直接返回结果，如果没有则需要调用此方法，并缓存结果
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson(){
        System.out.println("查询三级分类数据库");
        return getCatelogJsonFromDb();
    }

    public Map<String, List<Catelog2Vo>> getCatelogJson2() throws JsonProcessingException {

        /**
         * 1、缓存穿透，
         * 空结果缓存
         * 2、缓存雪崩
         * 过期时间加随机值
         * 3、缓存击穿
         * 加锁
         * 4、分布式问题
         * redis加锁
         * lua脚本解锁
         */
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        //缓存中保存的都是json格式数据，需要对json数据进行转换
        ObjectMapper objectMapper = new ObjectMapper();

        //1、从缓存中获取数据
        String catelogJson = ops.get("catelogJson");
        //2、如果缓存中没有数据就去数据库查出数据并保存到缓存中
        if (StringUtils.isEmpty(catelogJson)){

            //设置分布式事务锁 ,获取uuid
            String uuid = UUID.randomUUID().toString();
            Map<String, List<Catelog2Vo>> catelogJsonFromDb=null;
            for (;;){
                if (StringUtils.isNotEmpty(catelogJson)){
                    return objectMapper.readValue(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {});
                }
                // 加锁
                if (ops.setIfAbsent("lock",uuid,30L,TimeUnit.SECONDS)){
                    //加锁成功进入方法
                    catelogJsonFromDb = getCatelogJsonFromDb();

                    //解锁
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    stringRedisTemplate.execute(new DefaultRedisScript<Long>(script,Long.class),Arrays.asList("lock"),uuid);
                    break;
                }
                try {
                    // 没抢到锁休眠
                    Thread.sleep(200);
                    catelogJson = ops.get("catelogJson");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // 2.2、返回数据
            return catelogJsonFromDb;
        }
        System.out.println("缓存命中...");
        //3、对json数据进行反序列化操作并返回
        return objectMapper.readValue(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {});
    }

    /**
     * 从数据查询并封装数据
     * @return
     */

    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDb(){
        //试着优化
        List<CategoryEntity> listLevel1 = listWithTree();
        Map<String, List<Catelog2Vo>> level1 = listLevel1.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<Catelog2Vo> listLevel2 = null;
            // 二级
            List<CategoryEntity> level2 = v.getChildren();
            if (level2!= null && level2.size() != 0){
                listLevel2 = level2.stream().map(l2 ->{
                    Catelog2Vo catelog2Vo = new Catelog2Vo();
                    catelog2Vo.setId(l2.getCatId().toString());
                    catelog2Vo.setCatalog1Id(v.getCatId().toString());
                    catelog2Vo.setName(l2.getName());

                    //三级
                    List<CategoryEntity> children = l2.getChildren();
                    List<Catelog2Vo.Catelog3Vo> listlevel3 = null;
                    if (children != null && children.size() != 0){
                        listlevel3 = children.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo level3 = new Catelog2Vo.Catelog3Vo();
                            level3.setId(l3.getCatId().toString());
                            level3.setName(l3.getName());
                            level3.setCatalog2Id(l2.getCatId().toString());
                            return level3;
                        }).collect(Collectors.toList());
                    }
                    catelog2Vo.setCatalog3List(listlevel3);

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return listLevel2;
        }));
        return level1;
    }

    private List<Long> findParentPath(Long catelogId , List<Long> path){
        CategoryEntity categoryEntity = baseMapper.selectById(catelogId);
        if (categoryEntity.getParentCid() != 0){
            findParentPath(categoryEntity.getParentCid(),path);
        }
        path.add(catelogId);
        return path;
    }

    //根据父分类id查询子分类,并按照sort排序,小的在前，大的在后
    public List<CategoryEntity> getChildren(CategoryEntity categoryEntity,List<CategoryEntity> all){
        List<CategoryEntity> childCatList = all.stream()
                .filter(categoryEntity1 -> categoryEntity1.getParentCid().equals(categoryEntity.getCatId()))
                .map(categoryEntity1 -> {
                    categoryEntity1.setChildren(getChildren(categoryEntity1,all));
                    return categoryEntity1;
                })
                .sorted(Comparator.comparing(CategoryEntity::getSort))
                .collect(Collectors.toList());
        if (childCatList.size()==0){
            return null;
        }
        return childCatList;
    }
}