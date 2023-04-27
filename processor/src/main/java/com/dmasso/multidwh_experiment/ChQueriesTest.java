package com.dmasso.multidwh_experiment;

import com.dmasso.multidwh.data.OlapConnectionProperties;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import java.sql.*;
import java.util.concurrent.TimeUnit;

public class ChQueriesTest {
    private static final OlapConnectionProperties properties = new OlapConnectionProperties();

    // ram usage 200Mb-1Gb
    @Test
    public void testKV() {
        var query = "SELECT * from movie WHERE movie.id = 2654321;";
        query(query);
        // 300kk 100 ms

        // 3kk 183 ms
        // 10kk 256/799 ms
    }

    @Test
    public void testSimpleWhereStartsWith() {
        var query = "SELECT * FROM movie WHERE title like 'Love%'";
        query(query);
        // 300kk 306125 ms (306 s)

        // 3kk 1126 ms
        // 10kk 9464
    }

    @Test
    public void testRangeReadOneCol() {
        var query = "SELECT title FROM movie WHERE released > 2000;";
        query(query);
        // 300kk 2562 ms
        // 3kk 113 ms
        // 10kk 428/129
    }

    @Test
    public void testNERead() {
        var query = "SELECT * FROM movie WHERE title != 'Love';";
        query(query);
        // 3kk 125 ms
        // 10kk 1003/1615/169
    }

    @Test
    public void testCompoundPredicate() {
        var query = "SELECT * FROM movie WHERE title = 'Spider Man' and released = 2000;";
        query(query);
        // 3kk 1930 ms
        // 10kk 45278/50436
    }

    private void query(String query) {
        try (Connection con =
                     DriverManager.getConnection(properties.getUrl(), properties.getUser(), properties.getPassword())) {
            Statement statement = con.createStatement();
            StopWatch stopWatch = StopWatch.createStarted();
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println(stopWatch.getTime(TimeUnit.MILLISECONDS));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
