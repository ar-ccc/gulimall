package com.arccc.gulimall.member.controller;

import com.arccc.common.constant.MemberConstant;
import com.arccc.common.error.BizCodeEnume;
import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.R;
import com.arccc.common.vo.MemberRespVo;
import com.arccc.gulimall.member.entity.MemberEntity;
import com.arccc.gulimall.member.exception.PhoneExistsException;
import com.arccc.gulimall.member.exception.UserNameExistsException;
import com.arccc.gulimall.member.feign.CoupenFeignService;
import com.arccc.gulimall.member.service.MemberService;
import com.arccc.gulimall.member.vo.MemberLoginVo;
import com.arccc.gulimall.member.vo.MemberRegistryVo;
import com.arccc.gulimall.member.vo.SociaUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;


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
     * 用户注册
     * @param vo
     * @return
     */
    @PostMapping("/registry")
    public R registry(@RequestBody MemberRegistryVo vo){
        try {
            memberService.registry(vo);
        }catch (PhoneExistsException e){
            return R.error(BizCodeEnume.PHONE_EXCEPTION);
        }catch (UserNameExistsException e){
            return R.error(BizCodeEnume.USER_EXCEPTION);
        }

        return R.ok();
    }

    /**
     * 用户登录
     * @param vo
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo){
        MemberEntity m  = memberService.login(vo);
        if (m==null){
            return R.error(BizCodeEnume.USERNAME_OR_PASSWORD_EXCEPTION);
        }
        // TODO 登录成功处理
        MemberRespVo memberRespVo = new MemberRespVo();
        BeanUtils.copyProperties(m,memberRespVo);

        return R.ok().putDataObjectToJson(memberRespVo);
    }

    @PostMapping("/oauth/login")
    public R oauthLogin(@RequestBody SociaUser sociaUser, @RequestParam("type") MemberConstant.OauthLoginType type) throws Exception {
        MemberEntity m  = memberService.login(sociaUser,type);
        if (m==null){
            return R.error(BizCodeEnume.USERNAME_OR_PASSWORD_EXCEPTION);
        }
        // TODO 登录成功处理
        MemberRespVo memberRespVo = new MemberRespVo();
        BeanUtils.copyProperties(m,memberRespVo);
        return R.ok().putDataObjectToJson(memberRespVo);
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
