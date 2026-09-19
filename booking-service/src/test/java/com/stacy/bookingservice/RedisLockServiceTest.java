package com.stacy.bookingservice;

import com.stacy.bookingservice.service.RedisLockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisLockServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String,String> valueOperations;

    @Test
    void shouldAcquireLock(){

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.setIfAbsent(
                eq("lock:event:1"),
                anyString(),
                eq(Duration.ofSeconds(10))
        )).thenReturn(true);

        RedisLockService lockService =
                new RedisLockService(redisTemplate);

        String result = lockService.acquireLock("lock:event:1");
        assertNotNull(result);
        verify(valueOperations).setIfAbsent(
                eq("lock:event:1"),
                anyString(),
                eq(Duration.ofSeconds(10))
        );
    }


    @Test
    void shouldNotAcquireLockWhenAlreadyLocked() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.setIfAbsent(
                eq("lock:event:1"),
                anyString(),
                eq(Duration.ofSeconds(10))
        )).thenReturn(false);

        RedisLockService lockService =
                new RedisLockService(redisTemplate);

        String result = lockService.acquireLock("lock:event:1");

        assertNull(result);
    }
}
