package com.arccc.gulimall.member.service.impl;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.arccc.common.constant.MemberConstant;
import com.arccc.common.utils.HttpUtils;
import com.arccc.common.utils.PageUtils;
import com.arccc.common.utils.Query;
import com.arccc.gulimall.member.dao.MemberDao;
import com.arccc.gulimall.member.dao.MemberLevelDao;
import com.arccc.gulimall.member.entity.MemberEntity;
import com.arccc.gulimall.member.entity.MemberLevelEntity;
import com.arccc.gulimall.member.exception.PhoneExistsException;
import com.arccc.gulimall.member.exception.UserNameExistsException;
import com.arccc.gulimall.member.service.MemberService;
import com.arccc.gulimall.member.vo.MemberLoginVo;
import com.arccc.gulimall.member.vo.MemberRegistryVo;
import com.arccc.gulimall.member.vo.OauthVo;
import com.arccc.gulimall.member.vo.SociaUser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void registry(MemberRegistryVo vo) {
        MemberEntity memberEntity = new MemberEntity();
        phoneExists(vo.getPhone());
        userNameExists(vo.getUserName());

        //设置用户名密码手机号

        // 密码加密 MD5？
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        memberEntity.setUsername(vo.getUserName());
        memberEntity.setMobile(vo.getPhone());
        // 设置默认等级
        MemberLevelEntity default_status = memberLevelDao.selectOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
        memberEntity.setLevelId(default_status.getId());




        baseMapper.insert(memberEntity);
    }

    @Override
    public void phoneExists(String phone) throws PhoneExistsException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0){
            throw new PhoneExistsException();
        }
    }

    @Override
    public void userNameExists(String username) throws UserNameExistsException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0){
            throw new UserNameExistsException();
        }
    }

    /**
     *
     * @param vo 登录用户名密码
     * @return 用户信息
     */
    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).or().eq("mobile", loginacct));
        if (memberEntity == null){
            //用户名不存在
            return null;
        }
        // 获取到数据库的password字段
        String password1 = memberEntity.getPassword();
        // md5密码匹配
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(password, password1);
        if (matches){
            return memberEntity;
        }
        return null;
    }

    /**
     *  使用社交账号进行登录
     *  登录注册合并
     * @param sociaUser
     * @return
     */
    @Override
    public MemberEntity login(SociaUser sociaUser, MemberConstant.OauthLoginType type) throws Exception {
        if (MemberConstant.OauthLoginType.GITEE.equals(type)){
            return giteeLoginOrRegistry(sociaUser);
        }
        return null;
    }

    /**
     * gitee社交登录或注册
     * @param sociaUser
     * @return
     * @throws Exception
     */
    private MemberEntity giteeLoginOrRegistry(SociaUser sociaUser) throws Exception {
        Map<String ,String > querys = new HashMap<>();
        querys.put("access_token",sociaUser.getAccess_token());
        HttpResponse response = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", new HashMap<String, String>(), querys);
        if (response.getStatusLine().getStatusCode()==200){
            String s = EntityUtils.toString(response.getEntity());
            OauthVo.GiteeVo giteeVo = JacksonUtils.toObj(s, new TypeReference<OauthVo.GiteeVo>() {});
            // 通过数据库判断是否有该用户社交登录信息，字段为uid=type+id
            String uid = MemberConstant.OauthLoginType.GITEE.getType()+":"+giteeVo.getId();
            MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("uid", uid));
            if (memberEntity==null){
                // 没有本站用户信息，直接注册用户信息
                MemberEntity entity = new MemberEntity();
                entity.setUsername(giteeVo.getLogin());
                entity.setNickname(giteeVo.getName());
                entity.setCreateTime(new Date());
                entity.setUid(uid);
                entity.setLevelId(1L);
                baseMapper.insert(entity);
                memberEntity=entity;
            }
            // 有本站用户信息，登录成功


            return memberEntity;

        }
        return null;
    }


}