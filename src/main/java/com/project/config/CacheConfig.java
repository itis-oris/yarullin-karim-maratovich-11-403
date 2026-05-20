package com.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class CacheConfig {

  @Bean
  public CacheManager cacheManager(
      RedisConnectionFactory connectionFactory,
      @Value("${app.cache.prefix:tb:v1}") String cachePrefix,
      @Value("${spring.cache.redis.time-to-live:3600000}") long ttlMs) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.findAndRegisterModules();

    BasicPolymorphicTypeValidator typeValidator =
        BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("com.project")
            .allowIfSubType("java.math")
            .allowIfSubType("java.time")
            .allowIfSubType("java.util")
            .allowIfSubType("java.lang")
            .build();
    mapper.activateDefaultTypingAsProperty(
        typeValidator, ObjectMapper.DefaultTyping.EVERYTHING, "@class");
    GenericJackson2JsonRedisSerializer.registerNullValueSerializer(mapper, "@class");

    RedisCacheConfiguration config =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMillis(ttlMs))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer(mapper)))
            .computePrefixWith(cacheName -> cachePrefix + "::" + cacheName + "::");

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(config)
        .transactionAware()
        .build();
  }
}
