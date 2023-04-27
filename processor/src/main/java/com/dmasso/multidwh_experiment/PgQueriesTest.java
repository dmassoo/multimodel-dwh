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
        var query = "SELECT * from movie WHERE movie.id = 654321;";
        query(query);
        // 3kk 119 ms (2 cores)

        // 3kk 32 ms index released,title
        // 10kk 407m/203/41 ms index released,title
    }

    @Test
    public void testSimpleWhereStartsWith() {
        var query = "SELECT * FROM movie WHERE title like 'Love%'";
        query(query);
        // 3kk 13683 ms no index (2 cores)

        // 3kk 13797 index released,title
        // 10kk 77401/59385 index released,title
    }

    @Test
    public void testRangeReadOneCol() {
        var query = "SELECT title FROM movie WHERE released > 2000;";
        query(query);
        // 3kk 14791 ms no index (2 cores)
        // 10kk 65861/63882 index released,title
    }

    @Test
    public void testNERead() {
        var query = "SELECT * FROM movie WHERE title != 'Love';";
        query(query);
        // 3kk 16837 ms no index (2 cores)
        // 3kk 14315 ms index
        // 10kk 60879/63744 index released,title
    }

    @Test
    public void testCompoundPredicate() {
        var query = "SELECT * FROM movie WHERE title = 'Spider Man' and released = 2000;";
        query(query);
        // 3kk 2285 ms index
        // 10kk 58124/57598 index released,title
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
