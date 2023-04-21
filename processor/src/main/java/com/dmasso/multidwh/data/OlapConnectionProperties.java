package com.dmasso.multidwh.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "olap")
@EnableConfigurationProperties
public class OlapConnectionProperties implements ConnectionProperties {

    private final String url = "jdbc:ch://localhost/default?socket_timeout=3000000";
    private final String user = "default";
    private final String password = "password";
}
