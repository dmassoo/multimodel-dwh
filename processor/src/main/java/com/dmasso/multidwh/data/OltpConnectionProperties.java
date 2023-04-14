package com.dmasso.multidwh.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "oltp")
@EnableConfigurationProperties
public class OltpConnectionProperties implements ConnectionProperties {
    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String user = "postgres";
    private final String password = "password";
}
