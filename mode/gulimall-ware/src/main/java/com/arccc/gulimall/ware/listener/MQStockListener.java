package com.arccc.gulimall.ware.listener;

import com.arccc.common.vo.mq.OrderTo;
import com.arccc.common.vo.mq.WareOrderTaskTo;
import com.arccc.gulimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "stock.release.stock.queue")
@Slf4j
public class MQStockListener {

    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    public void unLock(Message message, WareOrderTaskTo wareOrderTaskTo, Channel channel) throws IOException {

        try {
            wareSkuService.unLock(wareOrderTaskTo);
            //消息成功抵达
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            //出现异常重新入队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            log.error(e.getMessage());
        }
    }

    /**
     * 订单关闭解锁库存
     * @param message
     * @param order
     * @param channel
     */
    @RabbitHandler
    public void handleOrderCloseRelease(Message message, OrderTo order, Channel channel) throws IOException {
        try {
            wareSkuService.unLock(order);
            //消息成功抵达
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            //出现异常重新入队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            log.error(e.getMessage());
        }
    }
}
