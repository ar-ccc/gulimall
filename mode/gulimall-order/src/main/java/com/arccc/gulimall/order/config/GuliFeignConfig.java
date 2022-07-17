package com.arccc.gulimall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * openfeign的增强，原始open发送请求不带有任何请求头信息，其他服务无法获取cookie，导致返回空值
 *
 */
@Configuration
public class GuliFeignConfig {
    @Bean
    RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //获取原始请求参数
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes!= null){
                    //获取原始请求
                    HttpServletRequest request = requestAttributes.getRequest();
                    //获取请求头中的cookie
                    String cookie = request.getHeader("Cookie");
                    //将cookie放出新请求中，同步cookie
                    requestTemplate.header("Cookie",cookie);
                }
            }
        };
    }
}
