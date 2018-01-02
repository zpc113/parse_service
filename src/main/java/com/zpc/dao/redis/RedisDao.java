package com.zpc.dao.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

/**
 * Created by 和谐社会人人有责 on 2017/12/19.
 */
@Component
public class RedisDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;

    public RedisDao() {
        jedisPool = new JedisPool("localhost" , 6379);
    }

    /**
     * url是否已爬取
     * 使用set添加url到redis服务器，自动去重
     * @param url
     * @param containerName
     * @return
     */
    public boolean isContains(String url , String containerName) {
        long setNum = 0;
        synchronized (jedisPool) {
            setNum = jedisPool.getResource().sadd(containerName , url);
        }
        if (setNum == 1)
            return false;
        return false;
    }

    /**
     * 新建去重队列
     * @param containerName
     */
    public void create(String containerName) {
        jedisPool.getResource().sadd(containerName);
    }

    /**
     * 删除去重队列
     * @param containerName
     */
    public void remove(String containerName) {
        jedisPool.getResource().del(containerName);
    }
}
