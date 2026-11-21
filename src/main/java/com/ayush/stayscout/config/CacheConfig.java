package com.ayush.stayscout.config;
import java.time.Duration; import org.springframework.cache.annotation.*; import org.springframework.context.annotation.*; import org.springframework.data.redis.cache.*;
@Configuration public class CacheConfig { @Bean RedisCacheConfiguration redisCacheConfiguration(){return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(60)).disableCachingNullValues();} }
