package com.dmasso.multidwh.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "neo4j")
@EnableConfigurationProperties
public class Neo4JConnectionProperties {
    private String uri = "bolt://localhost:7687";
}
