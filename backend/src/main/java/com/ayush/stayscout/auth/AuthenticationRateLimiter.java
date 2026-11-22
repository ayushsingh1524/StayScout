package com.ayush.stayscout.auth;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/** Limits anonymous authentication attempts without recording any credentials. */
@Component
public class AuthenticationRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationRateLimiter.class);
    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final StringRedisTemplate redis;
    private final long loginLimit;
    private final long registrationLimit;

    public AuthenticationRateLimiter(
            StringRedisTemplate redis,
            @Value("${app.auth.login-attempt-limit:10}") long loginLimit,
            @Value("${app.auth.registration-attempt-limit:5}") long registrationLimit
    ) {
        this.redis = redis;
        this.loginLimit = loginLimit;
        this.registrationLimit = registrationLimit;
    }

    public boolean loginAllowed(String clientIp) {
        return allowed("login", clientIp, loginLimit);
    }

    public boolean registrationAllowed(String clientIp) {
        return allowed("registration", clientIp, registrationLimit);
    }

    private boolean allowed(String action, String clientIp, long limit) {
        try {
            String key = "stayscout:auth:" + action + ":" + clientIp;
            Long attempts = redis.opsForValue().increment(key);
            if (attempts != null && attempts == 1) {
                redis.expire(key, WINDOW);
            }
            return attempts == null || attempts <= limit;
        } catch (DataAccessException exception) {
            // Authentication remains available if Redis is unavailable; no credentials are logged.
            log.warn("Authentication rate limiter unavailable: {}", exception.getClass().getSimpleName());
            return true;
        }
    }
}
