package com.github.krishantx.RLaaS.Repo;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.github.krishantx.RLaaS.Model.TokenBucket;

@Repository
public class RedisRepo {
    private static final Logger log = LoggerFactory.getLogger(RedisRepo.class);

    private final RedisTemplate<String, TokenBucket> redisTemplate;

    public static String createRedisKey(String apiKey, String method, String identifier, String endpoint) {
        return method + "|" + endpoint + "|" + identifier + "|" + apiKey;
    }

    public RedisRepo(RedisTemplate<String, TokenBucket> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public TokenBucket get(String key) {
        long start = System.nanoTime();
        try {
            return redisTemplate.opsForValue().get(key);
        } finally {
            log.info("redis.get took {} ms", elapsedMs(start));
        }
    }

    public void save(String key, TokenBucket value, long ttl) {
        long start = System.nanoTime();
        try {
            redisTemplate.opsForValue().set(
                key,
                value,
                Duration.ofMinutes(ttl)
            );
        } finally {
            log.info("redis.save took {} ms", elapsedMs(start));
        }
    }

    private long elapsedMs(long start) {
        return Duration.ofNanos(System.nanoTime() - start).toMillis();
    }
}
