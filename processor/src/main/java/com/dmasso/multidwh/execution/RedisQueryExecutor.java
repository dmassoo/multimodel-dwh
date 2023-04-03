package com.dmasso.multidwh.execution;


import com.dmasso.multidwh.common.enums.DbType;
import com.dmasso.multidwh.data.RedisConnectionProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class RedisQueryExecutor implements QueryExecutor<String> {
    private final RedisConnectionProperties connectionProperties;
    private JedisPool pool;

    @PostConstruct
    private void createPool() {
        pool = new JedisPool(connectionProperties.getHost(), connectionProperties.getPort());
    }

    @Override
    public Iterable<?> execute(String query) {
        String jsonValue;
        try (Jedis jedis = pool.getResource()) {
            jsonValue = jedis.get(query);
        }
        var result = new ArrayList<String>();
        result.add(jsonValue);
        return result;
    }

    @Override
    public DbType getType() {
        return DbType.KEY_VALUE;
    }
}
