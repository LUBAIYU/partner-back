package com.lzh.yupao.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    //地址
    private String host;
    //端口
    private String port;

    @Bean
    public RedissonClient redissonClient() {
        //创建配置
        Config config = new Config();
        String address = String.format("redis://%s:%s", host, port);
        config.useSingleServer().setAddress(address).setDatabase(1);
        //创建实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
