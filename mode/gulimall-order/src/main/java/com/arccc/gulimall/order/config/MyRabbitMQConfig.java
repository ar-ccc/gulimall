package com.arccc.gulimall.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

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
}
@Configuration
class RabbitTemplateConfig{
    @Autowired
    RabbitTemplate rabbitTemplate;
    @PostConstruct
    public void initTemplate(){
        /**
         * 消息成功发送到broker后回调
         */
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData 发送消息时放入的correlationData，消息成功发送到broker时，回调
             * @param ack 是否发送成功，ack和nack
             * @param cause 如果确认失败是的提示，用于nack
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {

                System.out.println("correlationData=>["+correlationData+"],ack=>["+ack+"],cause=>["+cause+"]");
            }
        });
        /**
         * 消息无法放入queue队列中时回调，如果消息无法放入队列建议设置死信队列
         */
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            /**
             * @param returned ReturnedMessage类是对原有接口ReturnCallback的封装，原有方法接口现如今已经废弃
             * 	private final Message message：消息本土
             * 	private final int replyCode：错误代码
             * 	private final String replyText：错误提示
             * 	private final String exchange：交换机
             * 	private final String routingKey：路由键
             */
            @Override
            public void returnedMessage(ReturnedMessage returned) {

                System.out.println("returned=>["+returned+"]");
            }
        });
    }
}
