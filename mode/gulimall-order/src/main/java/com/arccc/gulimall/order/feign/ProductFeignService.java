package com.arccc.gulimall.order.feign;

import com.arccc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-product-service")
public interface ProductFeignService {

    @PostMapping("/product/skuinfo/orderListItems")
    public R getOrderItemBySkuIds(@RequestBody List<Long> skuIds);
}
