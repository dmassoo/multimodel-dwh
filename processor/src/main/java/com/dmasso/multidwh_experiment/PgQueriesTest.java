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
        // 3kk 32 ms index title, released
        // 3kk 32 ms index title, released
        // 10kk 407m/203/41 ms index title, released
    }

    @Test
    public void testSimpleWhereStartsWith() {
        var query = "SELECT * FROM movie WHERE title like 'Love%'";
        query(query);

        // 3kk 13797 index
        // 3kk 13797 index title, released
        // 10kk 77401/59385
        // 10kk 55879/55331 index title, released
    }

    @Test
    public void testRangeReadOneCol() {
        var query = "SELECT title FROM movie WHERE released > 2000;";
        query(query);
        // 3kk 14791 ms no index (2 cores)
        // 10kk 65861/63882
        // 10kk 53600/52895 index title, released
    }

    @Test
    public void testNERead() {
        var query = "SELECT * FROM movie WHERE title != 'Love';";
        query(query);
        // 3kk 16837 ms no index (2 cores)
        // 3kk 14315 ms index
        // 10kk 60879/63744
        // 10kk 58851/56816 index title, released
    }

    @Test
    public void testCompoundPredicate() {
        var query = "SELECT * FROM movie WHERE title = 'Spider Man' and released = 2000;";
        query(query);
        // 3kk 2285 ms index
        // 10kk 58124/57598
        // 10kk 360 index title, released
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
