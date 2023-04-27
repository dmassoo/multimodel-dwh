package com.dmasso.multidwh_experiment;

import com.dmasso.multidwh.data.OltpConnectionProperties;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import java.sql.*;
import java.util.concurrent.TimeUnit;

public class PgQueriesTest {

    // ram usage 15-70 Mb
    private static final OltpConnectionProperties properties = new OltpConnectionProperties();

    @Test
    public void testKV() {
        var query = "SELECT * from movie WHERE movie.id = 3654321;";
        query(query);
    }

    @Test
    public void testSimpleWhereStartsWith() {
        var query = "SELECT * FROM movie WHERE title like 'Love%'";
        query(query);
    }

    @Test
    public void testRangeReadOneCol() {
        var query = "SELECT title FROM movie WHERE released > 2000;";
        query(query);
    }

    @Test
    public void testNERead() {
        var query = "SELECT * FROM movie WHERE title != 'Love';";
        query(query);
    }

    @Test
    public void testCompoundPredicate() {
        var query = "SELECT * FROM movie WHERE title = 'Spider Man' and released = 2000;";
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
