package com.database.repo;

import com.database.module.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yanbdong@cienet.com.cn
 * @since Sep 03, 2020
 */
@Repository
public class UserRedis {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void add(String key, Long time, User user) throws JsonProcessingException {
        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(user), time, TimeUnit.MINUTES);
    }

    public void add(String key, Long time, List<User> users) throws JsonProcessingException {
        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(users), time, TimeUnit.MINUTES);
    }

    public User get(String key) throws IOException {
        User user = null;
        String userJson = redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(userJson)) {
            user = objectMapper.readValue(userJson, User.class);
        }
        return user;
    }

    public List<User> getList(String key) throws IOException {
        List<User> ts = null;
        String listJson = redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(listJson)) {
            ts = objectMapper.readValue(listJson,
                    objectMapper.getTypeFactory().constructParametricType(List.class, User.class));
        }
        return ts;
    }

    public void delete(String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }
}
