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
     * 效率很低，后期需要优化
     * @param url
     * @param containerName
     * @return
     */
    public boolean isContains(String url , String containerName) {

        boolean end;
        synchronized (jedisPool) {
            end = jedisPool.getResource().lrange(containerName , 0 , Long.MAX_VALUE).contains(url);
            if (!end) {
                // 如果没有爬取过，就放到去重队列中
                jedisPool.getResource().lpush(containerName , url);
            }
        }
        return end;
    }

    /**
     * 新建去重队列
     * @param containerName
     */
    public void create(String containerName) {
        jedisPool.getResource().lpush(containerName);
    }

    /**
     * 删除去重队列
     * @param containerName
     */
    public void remove(String containerName) {
        jedisPool.getResource().del(containerName);
    }
}
