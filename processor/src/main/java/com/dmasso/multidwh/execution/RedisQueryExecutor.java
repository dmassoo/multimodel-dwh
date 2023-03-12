package com.dmasso.multidwh.execution;


import com.dmasso.multidwh.common.enums.DbType;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;

@Component
public class RedisQueryExecutor implements QueryExecutor<String> {
    // TODO: 12.03.2023 move connection props to config
    private final JedisPool pool = new JedisPool("localhost", 6379);

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
