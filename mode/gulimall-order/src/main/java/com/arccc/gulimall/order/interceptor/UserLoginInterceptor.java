package com.arccc.gulimall.order.interceptor;

import com.arccc.common.constant.AuthServerConstant;
import com.arccc.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserLoginInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberRespVo> memberVo = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/order/order/orderStatus/**", request.getRequestURI());
        boolean match1 = antPathMatcher.match("/payed/notify/**", request.getRequestURI());

        if (match||match1){
            return true;
        }

        MemberRespVo attribute = (MemberRespVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute==null){
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }else {
            memberVo.set(attribute);
            return true;
        }
    }
}
