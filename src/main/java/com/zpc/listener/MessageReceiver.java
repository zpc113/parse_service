package com.zpc.listener;

import com.zpc.dao.controlcenter.TaskConfigDao;
import com.zpc.dao.page.PageTableDao;
import com.zpc.dao.redis.RedisDao;
import com.zpc.dto.ControlExecutorOrder;
import com.zpc.dto.OrderMessage;
import com.zpc.dto.RoutingKey;
import com.zpc.dto.TaskOrder;
import com.zpc.entity.controlcenter.TaskConfig;
import com.zpc.executors.ExecutorPool;
import com.zpc.parse.Parse;
import com.zpc.send.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 和谐社会人人有责 on 2017/11/29.
 */
@Component
public class MessageReceiver implements MessageListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExecutorPool executorPool;
    @Autowired
    private Parse parse;
    @Autowired
    private TaskConfigDao taskConfigDao;
    @Autowired
    private SendMessage sendMessage;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private PageTableDao pageTableDao;
    /**
     * 接收消息
     * @param message
     */
    public void onMessage(Message message) {
        // 反序列化，获得对应的对象
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message.getBody());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            OrderMessage orderMessage = (OrderMessage) objectInputStream.readObject();

            String order = orderMessage.getOrder();
            // 获取taskid -> 获取脚本字符串
            final String containerName = orderMessage.getContainerName();
            long taskId = Long.parseLong(containerName.replace("QueueName" , "").split("_scheduleId_")[0]);
            TaskConfig taskConfig = taskConfigDao.findByTaskId(taskId);
            final String script = taskConfig.getScript();
            final String tableName = taskConfig.getDataTableName();
            if (ControlExecutorOrder.SEED.equals(order)) {
                // 新建解析线程池
                Map<String , ExecutorService> executorMap = executorPool.getExecutors();
                if (CollectionUtils.isEmpty(executorMap)) {
                    executorMap = new HashMap<String, ExecutorService>();
                    executorPool.setExecutors(executorMap);
                }

                ExecutorService executorService = Executors.newFixedThreadPool(5);
                executorPool.getExecutors().put(containerName , executorService);
                logger.info("新建解析线程池--------------------------->" + containerName);
                // 开始执行解析操作
                // 解析
                // 在redis中新建该list
                redisDao.create(containerName);
                int parseStatus = parse.parse(containerName , script , tableName);
                // 新建已解析的页面表
                String parsedTableName = containerName + "_parsed";
                pageTableDao.createTable(parsedTableName);
                if (parseStatus == 1) {
                    // 有新增的request，通知下载服务器新建下载线程
                    OrderMessage orderMessageToDownload = new OrderMessage();
                    orderMessageToDownload.setContainerName(containerName);
                    orderMessageToDownload.setOrder(ControlExecutorOrder.NEW);
                    sendMessage.sendDownMessage(orderMessageToDownload , RoutingKey.DOWNSERVICE_ROUTINGKEY);
                    logger.info("已通知下载服务初始化下载线程池" + containerName);
                } else {
                    // 没有新增的request，需要以下几个操作：1、停止任务 2、删除队列
                }
            } else if (ControlExecutorOrder.PARSE.equals(order)) {
                // 开始从数据库拿页面并解析
                ExecutorService executorService = executorPool.getExecutors().get(containerName);
                if (executorService != null) {
                    for (int i = 0 ; i < 10 ; i ++) {
                        executorService.execute(new Runnable() {
                            public void run() {
                                parse.parse(containerName , script , tableName);
                            }
                        });
                    }
                }
            } else if (ControlExecutorOrder.SUSPEND.equals(order)) {
                // 销毁线程池并删除redis队列
                this.destory(containerName , order);
            } else if (ControlExecutorOrder.DESTROY.equals(order)) {
                // 销毁线程池并删除redis队列
                this.destory(containerName , order);
                // 通知云队列服务删除队列
                OrderMessage orderMessageToQueue = new OrderMessage();
                orderMessageToQueue.setOrder(TaskOrder.STOP);
                String queueName = containerName.split("_")[0];
                orderMessageToQueue.setContainerName(queueName);
                try {
                    sendMessage.sendQueueMessage(orderMessageToQueue , RoutingKey.QUEUE_ROUTINGKEY);
                    logger.info("发送销毁云队列消息到云队列服务器成功-->" + orderMessageToQueue.toString());
                } catch (Exception e) {
                    logger.error(e.getMessage() , "发送销毁云队列消息到云队列服务器失败-->" + orderMessageToQueue.toString());
                }
            } else if (ControlExecutorOrder.COMPLETE.equals(order)) {
                // 任务已完成
                this.destory(containerName , order);
                // 通知云队列服务器删除队列
                OrderMessage orderMessageToQueue = new OrderMessage();
                orderMessageToQueue.setOrder(ControlExecutorOrder.COMPLETE);
                orderMessageToQueue.setContainerName(containerName.split("_")[0]);
                try {
                    sendMessage.sendQueueMessage(orderMessageToQueue , RoutingKey.QUEUE_ROUTINGKEY);
                    logger.info("发送任务完成云队列消息到云队列服务器成功-->" + orderMessageToQueue.toString());
                } catch (Exception e) {
                    logger.error(e.getMessage() , "发送任务完成云队列消息到云队列服务器成功-->" + orderMessageToQueue.toString());
                }
            } else if (ControlExecutorOrder.RECOVER.equals(order)) {
                // 恢复线程池
                ExecutorService executorService = Executors.newFixedThreadPool(5);
                executorPool.getExecutors().put(containerName , executorService);
                logger.info("新建解析线程池--------------------------->" + containerName);
                // 开始执行解析操作
                // 解析
                // 在redis中新建该list
                redisDao.create(containerName);
            }
        } catch (Exception e) {
            logger.error(e.getMessage() , "接收消息失败");
        }

    }

    /**
     * 销毁线程池公共方法
     * @param containerName
     */
    public void destory(String containerName , String order) {
        ExecutorService executorService = executorPool.getExecutors().get(containerName);
        executorPool.getExecutors().remove(containerName);
        if (order.equals(ControlExecutorOrder.COMPLETE)) {
            executorService.shutdown();
        } else {
            executorService.shutdownNow();
        }
        logger.info("线程池已销毁" + containerName);
        // 清除redis队列
        redisDao.remove(containerName);
    }

}
