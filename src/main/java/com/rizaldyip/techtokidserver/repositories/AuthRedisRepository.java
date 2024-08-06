package com.rizaldyip.techtokidserver.repositories;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class AuthRedisRepository {
    private final ValueOperations<String, String> valueOps;

    public AuthRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.valueOps = redisTemplate.opsForValue();
    }
    public void saveJWTKey(String email, String jwtKey, String keyPrefix, int expiration) {
        valueOps.set(keyPrefix + email, jwtKey, expiration, TimeUnit.HOURS);
    }

    public String getJWTKey(String email, String keyPrefix) {
        return valueOps.get(keyPrefix + email);
    }

    public void deleteJWTKey(String email, String keyPrefix) {
        valueOps.getOperations().delete(keyPrefix + email);
    }
}
