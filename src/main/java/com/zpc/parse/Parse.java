package com.zpc.parse;

import com.alibaba.fastjson.JSONObject;
import com.zpc.dao.controlcenter.ScheduleDao;
import com.zpc.dao.datacenter.DataInfoDao;
import com.zpc.dao.page.PageDao;
import com.zpc.dao.redis.RedisDao;
import com.zpc.dtcrawler.DtcrawlerScript;
import com.zpc.dtcrawler.Result;
import com.zpc.dto.OrderMessage;
import com.zpc.dto.Request;
import com.zpc.dto.RoutingKey;
import com.zpc.dto.TaskOrder;
import com.zpc.entity.controlcenter.Schedule;
import com.zpc.entity.controlcenter.TaskConfig;
import com.zpc.entity.datacenter.DataInfo;
import com.zpc.entity.page.Page;
import com.zpc.send.SendMessage;
import com.zpc.service.TaskConfigService;
import com.zpc.util.TransStrToObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 和谐社会人人有责 on 2017/12/11.
 */
@Component
public class Parse {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PageDao pageDao;

    @Autowired
    private ScheduleDao scheduleDao;

    @Autowired
    private DataInfoDao dataInfoDao;

    @Autowired
    private SendMessage sendMessage;

    @Autowired
    private RedisDao redisDao;
    /**
     * 解析页面
     * @param containerName
     * @return
     */
    public int parse(String containerName , String script , String tableName) {
        int parseStatus = 0;
        // 1、先从数据库拿到一条页面信息

        // 2、传入到脚本类中进行解析

        // 3、获取解析结果，获得result的内容

        Page page = null;
        // 获取该数据后立马删除它，原子操作，需要做同步处理
        synchronized (pageDao) {
            page = pageDao.getPage(containerName);
            pageDao.delete(page.getPageId() , containerName);
        }
        if (page == null) {return 0;}
        // 解析页面
        DtcrawlerScript dtcrawlerScript = (DtcrawlerScript) new TransStrToObject().trans(script);
        Result result = null;
        try {
            result = dtcrawlerScript.parse(page);
        } catch (Exception e) {
            // TODO 异常信息入库
            logger.error(e.getMessage() , "解析异常");
        }
        if (result != null) {
            // 将page放入已解析的表中
            pageDao.saveParsed(page.getPageStr() , page.getPageId() , containerName + "_parsed");
            // 获取result对象中的request信息，做去重，去重后将request发送到队列服务器，使用线程池发送。
            List<Request> childLinks = result.getChildLinks();
            if (childLinks != null && childLinks.size() > 0) {
                // 去重
                List<Request> endChildLinks = new ArrayList<Request>();
                for (Request request : childLinks) {
                    String url = request.getUrl();
                    if (!redisDao.isContains(url , containerName)) {
                        endChildLinks.add(request);
                    }
                }
                if (endChildLinks != null && endChildLinks.size() > 0) {
                    parseStatus = 1;
                    // 发送request到队列中
                    for (Request request : childLinks) {
                        OrderMessage orderMessage = new OrderMessage();
                        orderMessage.setOrder(TaskOrder.REQUEST);
                        orderMessage.setRequest(request);
                        String queueName = containerName.split("_scheduleId_")[0];
                        orderMessage.setContainerName(queueName);
                        try {
                            RestTemplate rest = new RestTemplate();
                            String url = "http://localhost:8080/queue/put";
                            boolean postResult = rest.postForObject(url , orderMessage , Boolean.class , (Object[]) null);
                            logger.info("发送request到队列服务器成功" + request.toString());
                            if (postResult) {
                                logger.info("url入队列成功" + orderMessage.toString());
                            } else {
                                logger.info("url入队列失败" + orderMessage.toString());
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage() , "发送request到队列服务器失败" + request.toString());
                        }
                    }
                }
            }
            // 获取result对象中的data信息，信息入库
            List<Map<String , String>> dataMaps = result.getDataMapList();
            // 获取调度信息
            long scheduleId = Long.parseLong(containerName.split("_scheduleId_")[1]);
            Schedule schedule = scheduleDao.findById(scheduleId);
            String dt = schedule.getDt();
            List<DataInfo> dataList = new ArrayList<DataInfo>();
            if (dataMaps != null && dataMaps.size() > 0) {
                // 信息入库
                for (Map<String , String> dataMap : dataMaps) {
                    String data = JSONObject.toJSONString(dataMap);
                    DataInfo dataInfo = new DataInfo();
                    dataInfo.setData_info(data);
                    dataInfo.setDt(dt);
                    dataInfo.setSchedule_id(scheduleId);
                    dataList.add(dataInfo);
                }
            }
            // 入库
            if (!CollectionUtils.isEmpty(dataList)) {
                logger.info("数据入库");
                try {
                    dataInfoDao.saveData(dataList , tableName);
                    logger.info("数据入库成功");
                } catch (Exception e) {
                    logger.info(e.getMessage() + "\n" + e.getStackTrace().toString() , "数据入库失败");
                }
            }
        }
        return parseStatus;
    }


}
