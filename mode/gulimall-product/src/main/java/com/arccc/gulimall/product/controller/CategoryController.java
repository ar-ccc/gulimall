package com.arccc.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.arccc.gulimall.product.entity.CategoryEntity;
import com.arccc.gulimall.product.service.CategoryService;
import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.R;



/**
 * 商品三级分类
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:44:27
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有分类和子分类，以树形结构组装起来
     */
    @RequestMapping("/list/tree")
    //@RequiresPermissions("product:category:list")
    public R list(){
        List<CategoryEntity> categoryEntityList = categoryService.listWithTree();


        return R.ok().put("data", categoryEntityList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }
    @RequestMapping("/update/sort")
    public R updateSort(@RequestBody CategoryEntity[] category){
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return R.ok();
    }

    /**
     * 删除
     * @RequestBody 获取请求体中的json数据，转换成对象
     */
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
   // @RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
        // 物理删除
        categoryService.removeMenuByIds(catIds);
        /*
        // 逻辑删除
        categoryService.updateStatus(catIds, 0);
         */
        return R.ok();
    }

}
