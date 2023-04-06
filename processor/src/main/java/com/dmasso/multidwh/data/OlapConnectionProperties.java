package com.dmasso.multidwh.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "olap")
@EnableConfigurationProperties
public class OlapConnectionProperties {
    private final String url = "jdbc:sqlite:C:\\Users\\Dmitrii\\Desktop\\BIG_DATA\\__3sem_Practice\\multimodel-dwh\\processor\\src\\main\\resources\\movies.db";
    private final String user = "postgres";
    private final String password = "password";
}
