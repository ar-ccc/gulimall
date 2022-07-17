package com.arccc.gulimall.order.feign;

import com.arccc.common.utils.R;
import com.arccc.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient("gulimall-member-service")
public interface MemberFeignService {
    @GetMapping("/member/memberreceiveaddress/{memberId}/addresses")
    @ResponseBody
    List<MemberAddressVo> getList(@PathVariable("memberId") Long memberId);

    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R getAddr(@PathVariable("id") Long id);
}
