package com.arccc.gulimall.search.feign;

import com.arccc.gulimall.search.entity.SkuInfoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("gulimall-product-service")
public interface ProductFeignService {
    @GetMapping("/product/skuinfo/getSkuByCatelogIdAndKeyword")
    public List<SkuInfoEntity> getSkuByCatelogIdAndKeyword(@RequestParam("catelog3Id") Long catelog3Id,@RequestParam("keyword") String keyword );
}
