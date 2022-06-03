package com.arccc.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.arccc.gulimall.member.feign.CoupenFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arccc.gulimall.member.entity.MemberEntity;
import com.arccc.gulimall.member.service.MemberService;
import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.R;

import javax.annotation.Resource;


/**
 * 会员
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 12:38:18
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Resource
    CoupenFeignService coupenFeignService;

    /**
     * 返回当前会员的所以优惠券
     */
    @RequestMapping("/coupons")
    public R coupons(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(1L);
        memberEntity.setNickname("arccc");

        R memberCoupons = coupenFeignService.memberCoupons();

        Object coupons = memberCoupons.get("coupons");

        return R.ok().put("member", memberEntity)
                     .put("coupons", coupons);

    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
