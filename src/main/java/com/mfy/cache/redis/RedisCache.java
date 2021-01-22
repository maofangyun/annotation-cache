package com.mfy.cache.redis;

import com.alibaba.fastjson.JSON;
import com.mfy.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisCache implements Cache {

	@Autowired
	private StringRedisTemplate redisTemplate;

    private static final String NAME = "redis";

    @Override
	public String getName() {
        return NAME;
    }

    @Override
    public String get(Object key) {
		return redisTemplate.opsForValue().get(JSON.toJSONString(key));
    }

    @Override
    public void put(Object key, Object value) {
		redisTemplate.opsForValue().set(JSON.toJSONString(key), JSON.toJSONString(value));
    }

    @Override
    public void put(Object key, Object value, int expire) {
		redisTemplate.opsForValue().set(JSON.toJSONString(key), JSON.toJSONString(value),expire, TimeUnit.SECONDS);
    }

    @Override
    public void evict(Object key) {
		redisTemplate.delete(JSON.toJSONString(key));
    }
}
