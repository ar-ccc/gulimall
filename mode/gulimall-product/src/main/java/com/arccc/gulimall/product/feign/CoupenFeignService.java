package com.arccc.gulimall.product.feign;

import com.arccc.common.to.SkuReductionTo;
import com.arccc.common.to.SpuBoundsTo;
import com.arccc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-coupon-service")
public interface CoupenFeignService {

    @PostMapping("/coupon/spubounds/save")
    public R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/save/info")
    R savSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
