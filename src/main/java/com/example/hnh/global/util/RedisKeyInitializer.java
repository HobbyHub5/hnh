package com.example.hnh.global.util;

import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedisKeyInitializer {

    private final RedisTemplate<String, String> redisTemplate;
    private final Environment environment;

    public RedisKeyInitializer(RedisTemplate<String, String> redisTemplate, Environment environment) {
        this.redisTemplate = redisTemplate;
        this.environment = environment;
    }

    @PostConstruct
    public void clearKeys() {
        String ddlAuto = environment.getProperty("spring.jpa.hibernate.ddl-auto");
        if("create".equalsIgnoreCase(ddlAuto) || "create-drop".equalsIgnoreCase(ddlAuto)) {
            Set<String> keys = redisTemplate.keys("board:view:*");
            if(keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        }
    }
}
