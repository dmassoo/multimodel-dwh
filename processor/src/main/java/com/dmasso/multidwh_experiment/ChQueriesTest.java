package com.dmasso.multidwh_experiment;

import com.dmasso.multidwh.data.OlapConnectionProperties;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import java.sql.*;
import java.util.concurrent.TimeUnit;

public class ChQueriesTest {
    private static final OlapConnectionProperties properties = new OlapConnectionProperties();

    @Test
    public void testKV() {
        var query = "SELECT * from movie WHERE movie.id = 654321;";
        query(query);
        // 300kk 100 ms
    }

    @Test
    public void testSimpleWhereStartsWith() {
        var query = "SELECT * FROM movie WHERE title like 'Love%'";
        query(query);
        // 300kk 306125 ms (306 s)
    }

    @Test
    public void testRangeRead() {
        var query = "SELECT title FROM movie WHERE released > 2000;";
        query(query);
        // 300kk 2562 ms
    }

    @Test
    public void testNERead() {
        var query = "SELECT * FROM movie WHERE title != 'Love';";
        query(query);
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
