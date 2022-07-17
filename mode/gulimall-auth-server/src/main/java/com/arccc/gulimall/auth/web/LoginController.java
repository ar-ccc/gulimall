package com.arccc.gulimall.auth.web;

import com.arccc.common.constant.AuthServerConstant;
import com.arccc.common.error.BizCodeEnume;
import com.arccc.common.utils.R;
import com.arccc.common.vo.MemberRespVo;
import com.arccc.gulimall.auth.feign.MemberFeignService;
import com.arccc.gulimall.auth.feign.ThirdPartyFeginService;
import com.arccc.gulimall.auth.vo.UserLoginVo;
import com.arccc.gulimall.auth.vo.UserRegistryVo;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ThirdPartyFeginService thirdPartyFeginService;

    @Autowired
    MemberFeignService memberFeignService;
    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        MemberRespVo attribute = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null){
            return "login";
        }else {
            return "redirect:http://gulimall.com";
        }
    }

    /**
     *  发送验证码
     * @param phone 目标手机号
     * @return 发送结果
     */
    @GetMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone) {
        //TODO 接口防刷

        // redis
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        //拼装key
        String key = AuthServerConstant.SMS_CODE_PREFIX + phone;
        //查看是否有key
        String redisCode = ops.get(key);
        if (StringUtils.isNotEmpty(redisCode)) {
            String[] s = redisCode.split("_");
            long l = Long.parseLong(s[1]);
            //判断是否超过60秒
            if (System.currentTimeMillis() - l < 60 * 1000) {
                // 60秒内不能发送
                return R.error(BizCodeEnume.SMS_EXCEPTION);
            }
        }
        //生成验证码
        String code = UUID.randomUUID().toString().substring(0, 5) + "_" + System.currentTimeMillis();

        //redis 缓存验证码
        ops.set(key, code, 15 * 60, TimeUnit.SECONDS);
        //发送验证码
        thirdPartyFeginService.sendCode(phone, code.split("_")[0]);
        return R.ok();
    }

    /**
     * 用户注册
     * RedirectAttributes redirectAttributes 重定向携带数据
     *
     * @param vo                 接受的注册信息
     * @param result             错误信息
     * @param redirectAttributes 重定向携带数据
     * @return 视图页
     */
    @PostMapping("/registry")
    public String registry(@Valid UserRegistryVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        System.out.println("进入方法了");
        // 数据校验
        if (result.hasErrors()) {
            Map<String, String> collect = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            //redirectAttributes.addFlashAttribute()重定向式携带数据
            redirectAttributes.addFlashAttribute("errors", collect);

            return "redirect:http://auth.gulimall.com/registry.html";
        }
        // 开始注册，调用远程服务
        //1、校验验证码
        String code = vo.getCode();
        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_PREFIX + vo.getPhone());
        if (StringUtils.isNotEmpty(s)) {
            //令牌机制
            stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_PREFIX + vo.getPhone());
            String[] s1 = s.split("_");
            if (s1[0].equals(code)) {

                //验证码校验成功，远程服务
                R r = memberFeignService.registry(vo);
                if (r.getCode()==0){
                    //成功
                    return "redirect:http://auth.gulimall.com/login.html";
                }else {
                    Map<String,String > map = new HashMap<>();
                    map.put("msg",r.getMeesage());
                    redirectAttributes.addFlashAttribute("errors", map);
                    return "redirect:http://auth.gulimall.com/registry.html";
                }
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", map);
                return "redirect:http://auth.gulimall.com/registry.html";
            }
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", map);
            return "redirect:http://auth.gulimall.com/registry.html";
        }

    }

    /**
     * 用户登录
     * @param vo
     * @return
     */
    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes attributes, HttpSession session){
        R login = memberFeignService.login(vo);
        if (login.getCode()!=0){
            //失败
            Map<String ,String > map = new HashMap<>();
            map.put("msg",login.getMeesage());

            attributes.addFlashAttribute("errors",map);
            return "redirect:http://auth.gulimall.com/login.html";
        }
        MemberRespVo memberRespVo = login.getDataObjectByTypeJson(new TypeReference<MemberRespVo>(){});
        session.setAttribute(AuthServerConstant.LOGIN_USER,memberRespVo);
        return "redirect:http://gulimall.com";
    }
}
