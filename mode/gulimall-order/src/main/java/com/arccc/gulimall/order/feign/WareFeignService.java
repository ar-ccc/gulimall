package com.arccc.gulimall.order.feign;

import com.arccc.common.utils.R;
import com.arccc.gulimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient("gulimall-ware-service")
public interface WareFeignService {
    @PostMapping("/ware/waresku/hasStock")
    @ResponseBody
    public Map<Long, Boolean> getHasStock(@RequestBody List<Long> skuIds);

    @GetMapping("/ware/wareinfo/getFare")
    public R getFare(@RequestParam("addrId") Long addrId);

    @PostMapping("/ware/waresku/lock")
    public R orderLockStock(@RequestBody WareSkuLockVo vo);
}
