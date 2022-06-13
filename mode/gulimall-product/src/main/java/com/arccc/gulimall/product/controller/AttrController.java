package com.arccc.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.arccc.gulimall.product.vo.AttrResponseVo;
import com.arccc.gulimall.product.vo.AttrVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arccc.gulimall.product.entity.AttrEntity;
import com.arccc.gulimall.product.service.AttrService;
import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.R;



/**
 * 商品属性
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:44:27
 */
@Slf4j
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 列表
     */
    @RequestMapping("/{type}/list/{id}")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("id") Long id,
                  @PathVariable("type") String type){
//        PageUtils page = attrService.queryPage(params);
        // 根据传入的id过滤和关键字key查询分页信息
        PageUtils page = attrService.queryBasePage(params,id,type);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);
        AttrResponseVo attrResponseVo = attrService.getInfo(attrId);

        return R.ok().put("attr", attrResponseVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
        attrService.saveAttr(attr);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
//		attrService.updateById(attr);
        attrService.updataAttr(attr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
