package com.arccc.gulimall.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableRedisHttpSession
public class GulimallSessionConfig {
    @Bean
    public CookieSerializer cookieSerializer(){
        //cookie相关配置
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        //设置cookie生效域名
        cookieSerializer.setDomainName("gulimall.com");
        //设置cookie名称
        cookieSerializer.setCookieName("GULIMALLSESSION");
        return cookieSerializer;
    }

    /**
     * 设置序列化机制
     * 这里必须配置name为：springSessionDefaultRedisSerializer
     * @return
     */
    @Bean(name = "springSessionDefaultRedisSerializer")
    public RedisSerializer<Object> redisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
}
