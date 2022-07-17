package com.arccc.gulimall.product.controller;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.R;
import com.arccc.gulimall.product.entity.AttrEntity;
import com.arccc.gulimall.product.entity.AttrGroupEntity;
import com.arccc.gulimall.product.service.AttrAttrgroupRelationService;
import com.arccc.gulimall.product.service.AttrGroupService;
import com.arccc.gulimall.product.service.AttrService;
import com.arccc.gulimall.product.service.CategoryService;
import com.arccc.gulimall.product.vo.AttrGroupRelationVo;
import com.arccc.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 属性分组
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:44:27
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    AttrService attrService;
    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private CategoryService categoryService;

    /**
     * /product/attrgroup/{catelogId}/withattr
     * 获取当前分类下的所有属性分组和所有属性
     */
    @GetMapping("/{catelogId}/withattr")
    public R getWithattr(@PathVariable("catelogId")Long catelogId){
        List<AttrGroupWithAttrsVo> list =  attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data",list);
    }

    //product/attrgroup/1/attr/relation 获取当前分组关联的所有属性
    @GetMapping("/{id}/attr/relation")
    public R attrRelation(@PathVariable("id")Long id){
        List<AttrEntity> list = attrService.getRelation(id);
        return R.ok().put("data", list);
    }

    /**
     * /product/attrgroup/{attrgroupId}/noattr/relation
     * 获取当前分类下没有被关联的属性
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String, Object> params,
                             @PathVariable("attrgroupId")Long attrGroupId){
        PageUtils page = attrService.getNoRelation(params,attrGroupId);
        return R.ok().put("page",page);
    }
    /**
     * product/attrgroup/attr/relation
     * 新增关联信息
     */
    @PostMapping("/attr/relation")
    public R addRelatsion(@RequestBody AttrGroupRelationVo[] vos){
        attrAttrgroupRelationService.saveBatch(vos);
        return R.ok();
    }
    /**
     * /product/attrgroup/attr/relation/delete
     * 删除 提交的关联信息；
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos){
        attrService.deleteAttrGroupRelations(vos);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId")Long catelogId){
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }

    @RequestMapping("/list")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryPage(params);
//        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
