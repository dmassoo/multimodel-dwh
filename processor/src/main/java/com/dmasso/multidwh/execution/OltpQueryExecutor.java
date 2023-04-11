package com.dmasso.multidwh.execution;


import com.dmasso.multidwh.common.enums.DbType;
import com.dmasso.multidwh.data.OltpConnectionProperties;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.dmasso.multidwh.common.enums.DbType.OLTP;

@Component
@RequiredArgsConstructor
public class OltpQueryExecutor implements QueryExecutor<String> {
    private final OltpConnectionProperties properties;
    @Override
    public Iterable<?> execute(String query) {
        try (Connection con =
                     DriverManager.getConnection(properties.getUrl(), properties.getUser(), properties.getPassword())) {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            List<String> result = new ArrayList<>();
            JSONObject currentEntry;
            while (resultSet.next()) {
                currentEntry = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    String name = rsmd.getColumnLabel(i);
                    Object value = resultSet.getObject(i);
                    currentEntry.put(name, value);
                }
                result.add(currentEntry.toString());
            }
            return result;
        } catch (SQLException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DbType getType() {
        return OLTP;
    }
}
