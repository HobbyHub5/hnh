package com.example.hnh.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Repository
public class RedisRefreshTokenRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisRefreshTokenRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addUserToToken(Long userId , String refreshToken){
        HashOperations<String, Long, String> hashOperations = redisTemplate.opsForHash();
        Map<Long, String> map = new HashMap<>();
        map.put(userId,refreshToken);

        hashOperations.put("token",userId , refreshToken);
        log.info("{}:{}", userId,hashOperations.get("token", userId));
    }

    public String getUserToken(Long userId){
        HashOperations<String, Long, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get("token", userId );
    }

    public boolean validationToken(Long userId , String refreshToken){
        return Objects.equals(refreshToken, getUserToken(userId));
    }

}
