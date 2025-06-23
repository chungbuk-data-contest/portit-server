package org.ssafy.datacontest.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class EmailRepository {

    private final String PREFIX = "student-email:"; // Redis 키 접두사
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public EmailRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void createEmailCertification(String email, String code) {
        int LIMIT_TIME = 5 * 60; // 5분
        redisTemplate.opsForValue()
                .set(PREFIX + email, code, Duration.ofSeconds(LIMIT_TIME));
    }

    public String getEmailCertification(String email) {
        return redisTemplate.opsForValue().get(PREFIX + email);
    }

    public void deleteEmailCertification(String email) {
        redisTemplate.delete(PREFIX + email);
    }

    public boolean hasKey(String email) {
        return redisTemplate.hasKey(PREFIX + email);
    }
}