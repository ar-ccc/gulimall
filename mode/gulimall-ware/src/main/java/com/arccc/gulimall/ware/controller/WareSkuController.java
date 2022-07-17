package com.arccc.gulimall.ware.controller;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.R;
import com.arccc.gulimall.ware.entity.WareSkuEntity;
import com.arccc.gulimall.ware.service.WareSkuService;
import com.arccc.gulimall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品库存
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 12:43:40
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/lock")
    public R orderLockStock(@RequestBody WareSkuLockVo vo){
        try {
            Boolean lockStockResult = wareSkuService.lock(vo);
            return R.ok().putDataObjectToJson(lockStockResult);
        }catch (RuntimeException e){
            return R.error().put("data",e.getMessage());
        }
    }

    /**
     * 检查Sku是否有库存
     */
    @PostMapping("/hasStock")
    @ResponseBody
    public Map<Long, Boolean> getHasStock(@RequestBody List<Long> skuIds){

        return wareSkuService.hasStocks(skuIds);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
