package com.arccc.gulimall.order.feign;

import com.arccc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("gulimall-coupon-service")
public interface CouponFeignService {
    @GetMapping("/coupon/spubounds/listByIds")
    R listByIds(@RequestParam("spuIds") List<Long> spuIds);
}
