package com.arccc.gulimall.member.feign;

import com.arccc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon-service")
public interface CoupenFeignService {
    @RequestMapping("/coupon/coupon/member/list")
    public R memberCoupons();
}
