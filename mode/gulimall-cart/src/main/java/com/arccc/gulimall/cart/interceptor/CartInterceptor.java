package com.arccc.gulimall.cart.interceptor;

import com.arccc.common.constant.AuthServerConstant;
import com.arccc.common.constant.CartConstant;
import com.arccc.common.vo.MemberRespVo;
import com.arccc.gulimall.cart.vo.UserInfoVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 执行目标方法前判断用户登录状态，并封装传递给controller目标请求
 */
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoVo> userInfoVoThreadLocal = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        UserInfoVo userInfoVo = new UserInfoVo();
        // 用户登录设置id
        if (memberRespVo != null){
            userInfoVo.setUserId(memberRespVo.getId());
        }
        //未登录设置key
        Cookie[] cookies = request.getCookies();
        if (cookies !=null) {
            for (Cookie cookie : cookies) {
                if (CartConstant.TEMP_USER_COOKIE_NAME.equals(cookie.getName())){
                    userInfoVo.setUserKey(cookie.getValue());
                }
            }
        }

        if (StringUtils.isEmpty(userInfoVo.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            userInfoVo.setUserKey(uuid);
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoVo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(60*60*24);
            response.addCookie(cookie);
        }


        userInfoVoThreadLocal.set(userInfoVo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        userInfoVoThreadLocal.remove();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
