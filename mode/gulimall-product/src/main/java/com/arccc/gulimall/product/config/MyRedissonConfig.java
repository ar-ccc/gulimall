package com.arccc.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRedissonConfig {

    @Bean
    public RedissonClient getRedissonClient(){

        Config config = new Config();
        //这里是单节点配置
        config.useSingleServer().setAddress("redis://192.168.10.100:6379");
/*
        // 这里是集群配置
        config.useClusterServers()
                //可以用"rediss://"来启用SSL连接
                .addNodeAddress("redis://127.0.0.1:7181");

 */

        return Redisson.create(config);
    }
}
