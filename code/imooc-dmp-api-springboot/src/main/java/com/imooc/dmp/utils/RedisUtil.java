package com.imooc.dmp.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //根据key获取Set中的所有值
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取set缓存的长度
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //返回多个集合的交集 sinter·
    public Set<Object> intersect(String key1, String key2) {
        return redisTemplate.opsForSet().intersect(key1,key2);
    }

    //新增sadd
    public void add(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    //返回多个集合的并集
    public Set<Object> union(String key1, String key2) {
        return redisTemplate.opsForSet().union(key1, key2);
    }

}
