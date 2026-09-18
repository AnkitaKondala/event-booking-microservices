package com.stacy.bookingservice.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;

    public RedisLockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String acquireLock(String key){

        String lockValue = UUID.randomUUID().toString();

        Boolean acquired = redisTemplate
                .opsForValue()
                .setIfAbsent(key,lockValue, Duration.ofSeconds(5));

        if(Boolean.TRUE.equals(acquired)) {
            return lockValue;
        }

        return null;
    }


    // this release lock can still cause race condition between get key and delete key

//    public void releaseLock(String key, String lockValue){
//
//        String currentValue = redisTemplate.opsForValue().get(key);
//
//        if(lockValue.equals(currentValue)) {
//            redisTemplate.delete(key);
//        }
//    }


    //LUA script which redis executes atomically
    public void releaseLock(String key, String lockValue) {

        String script =
                "if redis.call('get',KEYS[1]) == ARGV[1] " +
                        "then " +
                        "return redis.call('del',KEYS[1]) " +
                        "else " +
                        "return 0 " +
                        "end";

        redisTemplate.execute(
                new DefaultRedisScript<>(script,Long.class),
                List.of(key),
                lockValue);
    }

}
