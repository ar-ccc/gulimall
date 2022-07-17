package com.arccc.gulimall.ware.feign;

import com.arccc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-order-service")
public interface OrderFeignSerivce {
    @GetMapping("/order/order/orderStatus/{orderSn}")
    R getOrderStatus(@PathVariable String orderSn);
}
