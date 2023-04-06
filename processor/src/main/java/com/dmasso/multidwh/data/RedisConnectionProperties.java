package com.dmasso.multidwh.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "redis")
@EnableConfigurationProperties
public class RedisConnectionProperties {
    private String host = "localhost";
    private int port = 6379;
}
