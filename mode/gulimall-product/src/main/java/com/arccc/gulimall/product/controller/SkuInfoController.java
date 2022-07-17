package com.arccc.gulimall.product.controller;

import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.R;
import com.arccc.common.vo.OrderItemProductRespVo;
import com.arccc.gulimall.product.entity.SkuInfoEntity;
import com.arccc.gulimall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



/**
 * sku信息
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 10:44:26
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 查询所有订单需要的数据并封装返回
     * @param skuIds
     * @return
     */
    @PostMapping("/orderListItems")
    public R getOrderItemBySkuIds(@RequestBody List<Long> skuIds){
        List<OrderItemProductRespVo> orderItemProductRespVo = skuInfoService.getOrderItemBySkuIds(skuIds);
        return R.ok().putDataObjectToJson(orderItemProductRespVo);
    }
    @PostMapping("/getPrices")
    @ResponseBody
    public Map<Long,BigDecimal> getPrice(@RequestBody List<Long> skuIds){
        Collection<SkuInfoEntity> listByIds = skuInfoService.listByIds(skuIds);
        return listByIds.stream().collect(Collectors.toMap(SkuInfoEntity::getSkuId, SkuInfoEntity::getPrice));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:skuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo).putDataObjectToJson(skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:skuinfo:save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    @GetMapping("/getSkuByCatelogIdAndKeyword")
    @ResponseBody
    public List<SkuInfoEntity> getSkuByCatelogIdAndKeyword(@RequestParam(value = "catelog3Id",required = false) Long catelog3Id,@RequestParam(value = "keyword",required = false) String keyword ){

        return skuInfoService.getSkuByCatelogIdAndKeyword(catelog3Id,keyword);

    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:skuinfo:update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("product:skuinfo:delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
