package com.dmasso.arangodb;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.ContentType;
import com.arangodb.entity.BaseDocument;
import com.arangodb.serde.jackson.JacksonSerde;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class ArangoDbTest {

    @Test
    public void testKV() {
        var query = "SELECT * from movie WHERE movie.id = 123432;";
        query = """
                FOR m IN movie
                  FILTER m._key == 3654321
                  RETURN m
                """;
        query(query);
        //539
        //514
        //485
        //469
        //461
    }

    @Test
    public void testSimpleWhereStartsWith() {
        var query = "SELECT * FROM movie WHERE title like 'Love%'";
        query = """
                FOR m IN movie
                  FILTER m.title LIKE "Love%"
                  RETURN m
                """;
        query(query);
        //142857
        //72943

    }

    @Test
    public void testRangeReadOneCol() {
        var query = "SELECT title FROM movie WHERE released > 2000;";
        query = """
                FOR m IN movie
                  FILTER m.released > 2000
                  RETURN m.title
                """;
        query(query);
        //58114
        //57256
    }

    @Test
    public void testNERead() {
        var query = "SELECT * FROM movie WHERE title != 'Love';";
        query = """
                FOR m IN movie
                  FILTER m.title != "Love"
                  RETURN m
                """;
        query(query);
        //91433
        //72240
        //36066
        //63126
        //55329
    }

    @Test
    public void testCompoundPredicate() {
        var query = "SELECT * FROM movie WHERE title = 'Spider Man' and released = 2000;";
        query = """
                FOR m IN movie
                  FILTER m.title == "Spider Man" && m.released == 2000
                  RETURN m
                """;
        query(query);
        //46276
        //13211
        //12275
        //13481
    }


    private void query(String query) {
        ArangoDB arangoDB = new ArangoDB.Builder()
                .serde(JacksonSerde.of(ContentType.JSON))
                .password("rootpassword")
                .host("127.0.0.1", 8529)
                .build();
        StopWatch stopWatch = StopWatch.createStarted();
        var dbName = "mydb";
        try {
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, BaseDocument.class);
            System.out.println(stopWatch.getTime(TimeUnit.MILLISECONDS));
            System.out.println("\n\n---------------");
//            cursor.forEachRemaining(aDocument -> {
//                System.out.println("Key: " + aDocument.getKey());
//            });
        } catch (ArangoDBException e) {
            System.err.println("Failed to execute query. " + e.getMessage());
        }
        arangoDB.shutdown();
    }
}
