package com.github.krishantx.RLaaS.Service;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.krishantx.RLaaS.Model.CheckDTO;
import com.github.krishantx.RLaaS.Model.TokenBucket;
import com.github.krishantx.RLaaS.Repo.ClientRepo;
import com.github.krishantx.RLaaS.Repo.PostgresRepo;
import com.github.krishantx.RLaaS.Repo.RedisRepo;

@org.springframework.stereotype.Service
public class Service {
  private static final Logger log = LoggerFactory.getLogger(Service.class);

  private final RedisRepo redisRepo;
  private final ClientRepo clientRepo;
  private final PostgresRepo postgresRepo;

  public Service(RedisRepo redisRepo, ClientRepo clientRepo, PostgresRepo postgresRepo) {
    this.redisRepo = redisRepo;
    this.clientRepo = clientRepo;
    this.postgresRepo = postgresRepo;
  }

  public boolean check(CheckDTO requestDTO, String apiKey) {
    long start = System.nanoTime();
    String key = RedisRepo.createRedisKey(
        apiKey,
        requestDTO.getMethod(),
        requestDTO.getIdentifier(),
        requestDTO.getEndpoint());

    TokenBucket bucket = redisRepo.get(key);

    if (bucket == null) {
      bucket = loadBucketFromDatabase(apiKey, requestDTO.getEndpoint());

      // Fail closed or open depending on product choice.
      // Current behavior is fail open.
      if (bucket == null) {
        return false;
      }

      // First request consumes one token immediately.
      bucket.setTokens(bucket.getRateLimit() - 1);
      bucket.setTimestamp(now());
      redisRepo.save(key, bucket, 10);
      log.info("rate-limit check allowed cache_miss=true took {} ms", elapsedMs(start));
      return false;
    }

    long now = now();
    long elapsedSeconds = now - bucket.getTimestamp();

    int rateLimit = bucket.getRateLimit();
    float refillRatePerSecond = (float) rateLimit / 60;
    float availableTokens = Math.min(
        rateLimit,
        bucket.getTokens() + refillRatePerSecond * elapsedSeconds);

    if (availableTokens < 1) {
      // Important optimization:
      // Do not write Redis on denied requests.
      // This makes 429 responses much faster.
      log.info("rate-limit check denied cache_miss=false took {} ms", elapsedMs(start));
      return true;
    }

    bucket.setTokens(availableTokens - 1);
    bucket.setTimestamp(now);
    redisRepo.save(key, bucket, 10);

    log.info("rate-limit check allowed cache_miss=false took {} ms", elapsedMs(start));
    return false;
  }

  private TokenBucket loadBucketFromDatabase(String apiKey, String endpoint) {
    long start = System.nanoTime();
    try {
      return clientRepo.findByApiKey(apiKey)
          .flatMap(client -> postgresRepo.findByEndpointAndClient(endpoint, client))
          .map(endpointEntity -> {
            int rateLimit = endpointEntity.getRateLimit();
            return new TokenBucket(rateLimit, rateLimit, now());
          })
          .orElse(null);
    } finally {
      log.info("database bucket lookup took {} ms", elapsedMs(start));
    }
  }

  private long now() {
    return Instant.now().getEpochSecond();
  }

  private long elapsedMs(long start) {
    return Duration.ofNanos(System.nanoTime() - start).toMillis();
  }
}
