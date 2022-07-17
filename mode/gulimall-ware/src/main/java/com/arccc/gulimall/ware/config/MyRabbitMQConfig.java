package com.arccc.gulimall.ware.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Configuration
public class MyRabbitMQConfig {

    /**
     * 给容器放一个消息转化器
     * @return
     */
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper){
        return new Jackson2JsonMessageConverter(objectMapper);
    }
    /**
     * 库存服务交换机
     */
    @Bean
    public Exchange stockEventExchange(){
        return new TopicExchange("stock-event-exchange",true,false,null);
    }
    @Bean
    public Queue stockReleaseStockQueue(){
        return new Queue("stock.release.stock.queue",true,false,false,null);
    }
    @Bean
    public Queue stockDelayQueue(){
        Map<String,Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange","stock-event-exchange");
        map.put("x-dead-letter-routing-key","stock.release");
        map.put("x-message-ttl", 120000);
        return new Queue("stock.delay.queue",true,false,false,map);
    }
    @Bean
    public Binding stockReleaseBinding(){
        return new Binding("stock.release.stock.queue",Binding.DestinationType.QUEUE,
                "stock-event-exchange","stock.release.#",null);
    }
    @Bean
    public Binding stockLockedBinding(){
        return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE,
                "stock-event-exchange","stock.locked",null);
    }

}