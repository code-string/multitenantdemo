package com.example.multitenantdemo.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

@Configuration
public class RedisAppConfig {
    private final Environment env;

    public RedisAppConfig(Environment env){
        this.env = env;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(Objects.requireNonNull(env.getProperty("spring.redis.host")), Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.redis.port"))));
        return new JedisConnectionFactory(standaloneConfiguration);
    }

    @Bean
    public RedisTemplate redisTemplate(){
        RedisTemplate template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setEnableTransactionSupport(true);
        return template;
    }
}
