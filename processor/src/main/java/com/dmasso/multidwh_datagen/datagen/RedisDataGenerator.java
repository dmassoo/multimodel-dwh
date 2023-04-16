package com.dmasso.multidwh_datagen.datagen;

import com.dmasso.multidwh.data.RedisConnectionProperties;
import com.dmasso.multidwh_datagen.datagen.common.Movie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

import static com.dmasso.multidwh_datagen.datagen.common.Constants.DATA_SIZE;


public class RedisDataGenerator {
    public static final String KEY_PREFIX = "movie/id/";
    JedisPool pool;
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public RedisDataGenerator() {
        RedisConnectionProperties defaultConnProps = new RedisConnectionProperties();
        pool = new JedisPool(defaultConnProps.getHost(), defaultConnProps.getPort());
    }

    public void generateData(long size) {
        StopWatch stopWatch = StopWatch.createStarted();
        Jedis jedis = pool.getResource();
        for (long i = 1; i <= size; i++) {
            Pair<String, String> movieKV = generateRandomMovie(i);
            jedis.set(movieKV.getLeft(), movieKV.getRight());
        }
        System.out.println("Inserted " + size + " entries in Redis with prefix " + KEY_PREFIX);
        System.out.println("Time elapsed to write:" + stopWatch.getTime(TimeUnit.SECONDS));
        // about 90s/100k
        jedis.save();
        System.out.println("Time elapsed with save on disk:" + stopWatch.getTime(TimeUnit.SECONDS));
    }


    private Pair<String,String> generateRandomMovie(long id) {
        String title = RandomStringUtils.randomAlphabetic(3, 40);
        int released = RandomUtils.nextInt(1900, 2023);
        String tagline = RandomStringUtils.randomAlphabetic(15, 90);

        try {
            return Pair.of(KEY_PREFIX + id, OBJECT_MAPPER.writeValueAsString(new Movie(id, title, released, tagline)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new RedisDataGenerator().generateData(DATA_SIZE);
    }
}
