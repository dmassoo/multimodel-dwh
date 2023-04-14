package com.dmasso.multidwh_datagen.datagen;


import com.dmasso.multidwh.data.ConnectionProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.concurrent.TimeUnit;

public interface RelationalDataGenerator {
    long DATA_SIZE = 100000;
    int BATCH_SIZE = 1000;

    ConnectionProperties getConnectionProperties();

    default void generateData() {
        StopWatch started = StopWatch.createStarted();
        ConnectionProperties connectionProperties = getConnectionProperties();
        try {
            Connection conn = DriverManager.getConnection(
                    connectionProperties.getUrl(),
                    connectionProperties.getUser(),
                    connectionProperties.getPassword()
            );
            PreparedStatement st = conn.prepareStatement("INSERT INTO movie VALUES (?, ?, ?, ?)");
            String title;
            int released;
            String tagline;
            for (long i = 0; i < DATA_SIZE; i++) {
                title = RandomStringUtils.randomAlphabetic(3, 40);
                released = RandomUtils.nextInt(1900, 2023);
                tagline = RandomStringUtils.randomAlphabetic(15, 90);

                st.setLong(1, i);
                st.setString(2, title);
                st.setInt(3, released);
                st.setString(4, tagline);
                st.addBatch();
                if (i % getBatchSize() == 0) {
                    System.out.println("Generated " + i + " tuples");
                    st.executeLargeBatch();
                }
            }
            st.executeLargeBatch();

            conn.close();
            System.out.println("Time elapsed to generate " + DATA_SIZE + " tuples: " + started.getTime(TimeUnit.MILLISECONDS) + "ms");
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
    }

    default int getBatchSize() {
        return BATCH_SIZE;
    }
}
