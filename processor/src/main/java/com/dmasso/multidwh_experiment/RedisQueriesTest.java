package com.dmasso.multidwh_experiment;

import com.dmasso.multidwh.data.RedisConnectionProperties;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

public class RedisQueriesTest {
    private static final RedisConnectionProperties properties = new RedisConnectionProperties();
    // ram usage 650 Mb for 3kk
    @Test
    public void testKV() {
        var query = "movie/id/7484341";
        query(query);
    }

    private void query(String query) {
        String jsonValue;
        try (Jedis jedis = new Jedis(properties.getHost(), properties.getPort())) {
            StopWatch stopWatch = StopWatch.createStarted();
            jsonValue = jedis.get(query);
            System.out.println(stopWatch.getTime(TimeUnit.MILLISECONDS));
        }
    }
}
