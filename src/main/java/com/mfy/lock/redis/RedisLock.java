package com.mfy.lock.redis;

import com.mfy.lock.CacheLock;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLock implements CacheLock, InitializingBean {

    private static final String NAME = "redis";

    @Override
    public String getName() {
        return NAME;
    }

    @Autowired
    private Environment environment;

    private RedissonClient redissonClient;

    private RLock lock;

    @Override
    public void lock(long leaseTime, TimeUnit unit,String key) {
        lock = redissonClient.getLock(key);
        lock.lock(leaseTime,unit);
    }

    @Override
    public void unlock() {
        if(lock.isLocked() && lock.isHeldByCurrentThread()){
            lock.unlock();
        }
    }

    @Override
    public void afterPropertiesSet() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + environment.getProperty("spring.redis.host") + ":" + environment.getProperty("spring.redis.port"));
        config.useSingleServer().setConnectionPoolSize(Integer.valueOf(environment.getProperty("spring.redis.lettuce.pool.max-active")));
        config.useSingleServer().setConnectionMinimumIdleSize(Integer.valueOf(environment.getProperty("spring.redis.lettuce.pool.max-idle")));
        redissonClient = Redisson.create(config);
    }
}
