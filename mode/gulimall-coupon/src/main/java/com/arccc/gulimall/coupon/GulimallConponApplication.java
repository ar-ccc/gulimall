package com.arccc.gulimall.coupon;

import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
public class GulimallConponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallConponApplication.class, args);
    }

}
