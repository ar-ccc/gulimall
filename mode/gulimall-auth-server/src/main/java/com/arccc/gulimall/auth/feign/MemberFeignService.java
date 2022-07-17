package com.arccc.gulimall.auth.feign;

import com.arccc.common.constant.MemberConstant;
import com.arccc.common.utils.R;
import com.arccc.gulimall.auth.vo.SociaUser;
import com.arccc.gulimall.auth.vo.UserLoginVo;
import com.arccc.gulimall.auth.vo.UserRegistryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gulimall-member-service")
public interface MemberFeignService {
    @PostMapping("/member/member/registry")
    R registry(@RequestBody UserRegistryVo vo);
    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth/login")
    R oauthLogin(@RequestBody SociaUser sociaUser, @RequestParam("type") MemberConstant.OauthLoginType type) throws Exception;
}
