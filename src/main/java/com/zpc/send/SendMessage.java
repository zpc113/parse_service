package com.zpc.send;

import com.zpc.dto.ControlExecutorOrder;
import com.zpc.dto.OrderMessage;
import com.zpc.dto.TaskOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by 和谐社会人人有责 on 2017/12/7.
 * 发送消息
 */
@Component
public class SendMessage {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 往下载服务器发送对应指令
     * @param message
     */
    public void sendDownMessage(OrderMessage message , String routingKey) {
        try {
            rabbitTemplate.convertAndSend(routingKey , message);
            if (ControlExecutorOrder.SEED.equals(message.getOrder())) {
                logger.info("已通知下载服务器开始下载种子页面 " + message.getRequest().toString());
            } else if (ControlExecutorOrder.NEW.equals(message.getOrder())) {
                logger.info("已通知下载服务器新建线程池 " + message.toString());
            }
        } catch (Exception e) {
            logger.error(e.getMessage() , "往下载服务器发送消息失败" + message.toString());
        }
    }

    /**
     * 往下载解析服务器发送对应指令
     * @param message
     * @param routingKey
     */
    public void sendParseMessage(OrderMessage message , String routingKey) {
        try {
            rabbitTemplate.convertAndSend(routingKey , message);
            if (ControlExecutorOrder.NEW.equals(message.getOrder())) {
                logger.info("已通知解析服务器新建解析线程池 " + message.getRequest().toString());
            } else if (ControlExecutorOrder.NEW.equals(message.getOrder())) {
                logger.info("已通知解析服务器销毁线程池 " + message.toString());
            }
        } catch (Exception e) {
            logger.error(e.getMessage() , "往解析服务器发送消息失败" + message.toString());
        }
    }


    /**
     * 往队列解析服务器发送对应指令
     * @param message
     * @param routingKey
     */
    public void sendQueueMessage(OrderMessage message , String routingKey) {
        try {
            rabbitTemplate.convertAndSend(routingKey , message);
            if (TaskOrder.STOP.equals(message.getOrder())) {
                logger.info("已通知队列服务器销毁队列 " + message.getRequest().toString());
            }
        } catch (Exception e) {
            logger.error(e.getMessage() , "往队列服务器发送消息失败" + message.toString());
        }
    }
}
