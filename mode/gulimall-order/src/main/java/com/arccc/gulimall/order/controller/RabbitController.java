package com.arccc.gulimall.order.controller;

import com.arccc.gulimall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RabbitController {
    @Autowired
    RabbitTemplate template;
    @GetMapping("/send/{num}")
    public String sendMessage(@PathVariable("num")Long num){
        //发是 num 条数据
        for (int i = 0; i < num; i++) {
            OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
            entity.setName("消息"+i);
            /**
             * String exchange：交换机，不携带默认为 “”
             * String routingKey：路由键，不携带默认为 “”
             * final Object message：消息内容，必须携带
             * final MessagePostProcessor messagePostProcessor：其他参数，可以不写
             * @Nullable CorrelationData correlationData：可以才发送消息成功后回调的参数，id必须唯一，不携带默认为null
             */
            template.convertAndSend("hello_java","", entity,new CorrelationData(UUID.randomUUID().toString()));
        }
        return "ok";
    }
}
