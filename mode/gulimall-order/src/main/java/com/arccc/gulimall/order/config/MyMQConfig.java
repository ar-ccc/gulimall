package com.arccc.gulimall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Bean 可以直接给消息队列里面创建这些交换机和绑定关系（MQ内没有的情况下）
 */
@Configuration
public class MyMQConfig {

    //订单交换机
    @Bean
    public Exchange orderEventExchange(){
       return new TopicExchange("order-event-exchange",true,false);
    }
    //订单释放队列
    @Bean
    public Queue orderReleaseQueue(){
        return new Queue("order.release.order.queue",true,false,false);
    }
    //延迟队列，死信队列
    @Bean
    public Queue orderDelayQueue(){
        Map<String,Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange","order-event-exchange");
        map.put("x-dead-letter-routing-key","order.release.order");
        map.put("x-message-ttl", 60000);
        return new Queue("order.delay.queue",true,false,false,map);
    }
    //订单创建延迟队列绑定
    @Bean
    public Binding orderCreateBinding(){
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange","order.create.order",null);
    }
    //订单释放延迟队列绑定
    @Bean
    public Binding orderReleaseBinding(){
        return new Binding("order.release.order.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange","order.release.order",null);
    }
    //订单释放库存
    @Bean
    public Binding orderReleaseOtherBinding(){
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange","order.release.other.#",null);
    }
}
