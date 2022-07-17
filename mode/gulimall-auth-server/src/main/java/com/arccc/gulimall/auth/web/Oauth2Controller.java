package com.arccc.gulimall.auth.web;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.arccc.common.constant.AuthServerConstant;
import com.arccc.common.constant.MemberConstant;
import com.arccc.common.utils.HttpUtils;
import com.arccc.common.utils.R;
import com.arccc.common.vo.MemberRespVo;
import com.arccc.gulimall.auth.feign.MemberFeignService;
import com.arccc.gulimall.auth.vo.SociaUser;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("oauth2.0")
public class Oauth2Controller {
    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/gitee/success")
    public String gitee(@RequestParam("code") String code, HttpSession session, HttpServletResponse response1) throws Exception {
        System.out.println(code);
        //1、通过code环球access_token，获取token成功则返回首页
        Map<String,String> headers = new HashMap<>();
        Map<String ,String > bodys = new HashMap<>();
        bodys.put("code",code);
        bodys.put("client_id","aa0cf49b554994e47b88326b1dfce8ccc66527b6be81950c483cbd5e68cc8934");
        bodys.put("redirect_uri","http://auth.gulimall.com/oauth2.0/gitee/success");
        bodys.put("client_secret","ebe18f9b870c3b08cc8e110a8af0402ee07a63dcca9edf04eba2e4e65e563f33");
        bodys.put("grant_type","authorization_code");

        HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post", headers, null, bodys);

        if (response.getStatusLine().getStatusCode()==200) {
            //登录成功跳回首页
            String s = EntityUtils.toString(response.getEntity());
            SociaUser sociaUser = JacksonUtils.toObj(s, new TypeReference<SociaUser>() {
            });
            // 登录或者注册用户
            R r = memberFeignService.oauthLogin(sociaUser, MemberConstant.OauthLoginType.GITEE);
            if (r.getCode()==0){
                System.out.println("*************成功*************");
                //TODO 1、解决子域名访问session问题
                //TODO 2、解决session中对象存放为jdk序列化问题
                String id = session.getId();
                System.out.println("indexSessionId=>"+id);
                MemberRespVo data = r.getDataObjectByTypeJson(new TypeReference<MemberRespVo>() {});
                session.setAttribute(AuthServerConstant.LOGIN_USER,data);
            }
            return "redirect:http://gulimall.com";
        }else {
            // 登录失败
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
