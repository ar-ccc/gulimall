package com.arccc.gulimall.cart.feign;

import com.arccc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient("gulimall-product-service")
public interface ProductFeignService {
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);
    @GetMapping("/product/skusaleattrvalue/getSaleList/{skuId}")
    List<String > getSkuSale(@PathVariable("skuId")Long id);
    @PostMapping("/product/skuinfo/getPrices")
    @ResponseBody
    public Map<Long,BigDecimal> getPrice(@RequestBody List<Long> skuIds);
}
