package com.arccc.gulimall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"com.arccc.gulimall.product.dao"})
public class MybatisConfig {
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置页面大于最大请求页后的操作，true调回首页，false 继续请求 ，默认false
        paginationInterceptor.setOverflow(true);
        // 设置最大单页数量限制，默认500条，-1条不限制
        paginationInterceptor.setLimit(1000);
        return paginationInterceptor;
    }
}
