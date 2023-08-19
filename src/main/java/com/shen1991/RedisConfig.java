package com.shen1991;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.io.Serializable;

@Configuration
@ComponentScan("com.shen1991")
public class RedisConfig {
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<Serializable, Serializable> redisSerializableTemplate() {
        final RedisTemplate<Serializable, Serializable> template = new RedisTemplate<Serializable, Serializable>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(template.getKeySerializer());
        template.setValueSerializer(template.getValueSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, String> redisStringTemplate() {
        final RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(template.getKeySerializer());
        template.setValueSerializer(template.getValueSerializer());
        return template;
    }

}
