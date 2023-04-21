package com.dmasso.multidwh_datagen.datagen;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.dmasso.multidwh_datagen.datagen.common.Constants.DATA_SIZE;

public class CsvDataGenerator {

    public static final double STEP = 10000;

    public static void main(String[] args) throws IOException {
        StopWatch started = StopWatch.createStarted();
        String title;
        int released;
        String tagline;
        try (FileWriter csvWriter = new FileWriter("E:\\master-thesis-data\\csv\\movie-tiny.csv")) {
            for (long i = 1; i <= 10; i++) {
                title = RandomStringUtils.randomAlphabetic(3, 40);
                released = RandomUtils.nextInt(1900, 2023);
                tagline = RandomStringUtils.randomAlphabetic(40, 90);
                csvWriter.append(String.valueOf(i));
                csvWriter.append(",");
                csvWriter.append(title);
                csvWriter.append(",");
                csvWriter.append(String.valueOf(released));
                csvWriter.append(",");
                csvWriter.append(tagline);
                csvWriter.append("\n");
                if (i % STEP == 0) {
                    System.out.println("Generated " + i + " tuples");
                }
            }
        }
        System.out.println("Time elapsed to generate " + DATA_SIZE + " tuples: " + started.getTime(TimeUnit.SECONDS) + "s");
    }

}
