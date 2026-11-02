package com.sengang.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host:127.0.0.1}")
    private String host;
    @Value("${spring.redis.port:6379}")
    private int port;
    @Value("${spring.redis.password:}")
    private String password;

    @Bean
    public RedissonClient redissonClient(){
        //配置类
        Config config = new Config();
        //配置配置类
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password);
        //创建Redisson客户端
        return Redisson.create(config);
    }
}
