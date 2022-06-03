package com.arccc.gulimall.member.dao;

import com.arccc.gulimall.member.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author arccc
 * @email c23zxcvbnm@163.com
 * @date 2022-06-03 12:38:18
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
