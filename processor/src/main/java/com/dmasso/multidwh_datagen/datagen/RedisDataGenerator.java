package com.dmasso.multidwh_datagen.datagen;

import com.dmasso.multidwh.data.RedisConnectionProperties;
import com.dmasso.multidwh_datagen.datagen.common.Movie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class RedisDataGenerator {
    public static final String KEY_PREFIX = "movie/id/";
    public static final int DATA_SIZE = 1000;
    JedisPool pool;
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public RedisDataGenerator() {
        RedisConnectionProperties defaultConnProps = new RedisConnectionProperties();
        pool = new JedisPool(defaultConnProps.getHost(), defaultConnProps.getPort());
    }

    public void generateData(long size) {
        Jedis jedis = pool.getResource();
        for (long i = 1; i <= size; i++) {
            Pair<String, String> movieKV = generateRandomMovie(i);
            jedis.set(movieKV.getLeft(), movieKV.getRight());
        }
        System.out.println("Inserted " + size + " entries in Redis with prefix " + KEY_PREFIX);
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
//        RedisDataGenerator redisDataGenerator = new RedisDataGenerator();
//        String s = redisDataGenerator.pool.getResource().get("movie/id/432");
//        System.out.println(s);
    }
}
