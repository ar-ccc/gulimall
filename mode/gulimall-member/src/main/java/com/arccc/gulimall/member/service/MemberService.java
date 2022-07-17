package com.arccc.gulimall.member.service;

import com.arccc.common.constant.MemberConstant;
import com.arccc.common.utils.PageUtils;
import com.arccc.gulimall.member.entity.MemberEntity;
import com.arccc.gulimall.member.exception.PhoneExistsException;
import com.arccc.gulimall.member.exception.UserNameExistsException;
import com.arccc.gulimall.member.vo.MemberLoginVo;
import com.arccc.gulimall.member.vo.MemberRegistryVo;
import com.arccc.gulimall.member.vo.SociaUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 会员
 *
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 12:38:18
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void registry(MemberRegistryVo vo);
    void phoneExists(String phone) throws PhoneExistsException;
    void userNameExists(String username) throws UserNameExistsException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SociaUser sociaUser, MemberConstant.OauthLoginType type) throws Exception;
}

