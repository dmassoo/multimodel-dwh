package com.dmasso.arangodb;


import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ContentType;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.serde.jackson.JacksonSerde;
import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;

public class ArangoDbDataGen {

    private final ArangoDB arangoDB = new ArangoDB.Builder()
            .serde(JacksonSerde.of(ContentType.JSON))
            .password("rootpassword")
                .host("127.0.0.1", 8529)
                .build();

    @SneakyThrows
    public static void main(String[] args) {
        ArangoDbDataGen arangoDbDataGen = new ArangoDbDataGen();
        String dbName = "mydb";
        try {
            arangoDbDataGen.arangoDB.createDatabase(dbName);
            System.out.println("Database created: " + dbName);
        } catch (ArangoDBException e) {
            System.err.println("Failed to create database: " + dbName + "; " + e.getMessage());
        }
        CountDownLatch latch = new CountDownLatch(2);
        Thread threadGen = new Thread(() -> {
            System.out.println("Started datagen kv");
            arangoDbDataGen.generateKvData(dbName);
            latch.countDown();
            System.out.println("Finished datagen kv");
        });

        Thread threadGenKv = new Thread(() -> {
            System.out.println("Started datagen");
            arangoDbDataGen.generateData(dbName);
            latch.countDown();
            System.out.println("Finished datagen");
        });
        threadGenKv.start();
        threadGen.start();
        latch.await();
        System.out.println("All the data is generated");
        arangoDbDataGen.arangoDB.shutdown();
    }
    //data is loaded via arangoimport eventually
    private void generateKvData(String dbName) {

    }
    private void generateData(String dbName) {

        createMovieCollection(dbName);
    }

    private void createMovieCollection(String dbName) {
        String collectionName = "movie";
        try {
            CollectionEntity myArangoCollection = arangoDB.db(dbName).createCollection(collectionName);
            System.out.println("Collection created: " + myArangoCollection.getName());
        } catch (ArangoDBException e) {
            System.err.println("Failed to create collection: " + collectionName + "; " + e.getMessage());
        }
    }
}
