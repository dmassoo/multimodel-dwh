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

import static com.dmasso.multidwh.common.enums.DbType.OLAP;

@Component
@RequiredArgsConstructor
public class SqlQueryExecutor implements QueryExecutor<String> {
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
            // The column count starts from 1
//            HashMap<String, String> colNameToType = new HashMap<>();
//            for (int i = 1; i <= columnCount; i++) {
//                String name = rsmd.getColumnLabel(i);
//                String type = rsmd.getColumnClassName(i);
//                colNameToType.put(name, type);
//            }
//            List<JSONObject> result = new ArrayList<>();
//            while (resultSet.next()) {
//                // TODO: 11.03.2023 it is required to get column types (java compatible) in order to get resul
//                JSONObject json = new JSONObject();
//                for (String name : colNameToType.keySet()) {
//                    String type = colNameToType.get(name);
//
////                    Class<?> javaType = mapDbColTypeToJdbcType(type);
//
//                    Object colValue = null;
//                    try {
//                        json.put(name, colValue);
//                    } catch (JSONException e) {
//                        // NoOP
//                    }
//                }
//                result.add(json);
//            }
            return result;
        } catch (SQLException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

//    private Class<?> mapDbColTypeToJdbcType(String colType) {
//        // TODO: 12.03.2023 define mapping from posgresql types to jdbc resultset methods
//        // such as getString(colName) in case of char/varchar/text columns
//        // String, Int, Long, Float, Double BigDecimal, Boolean, Date, Timestamp
//        // result.getObject("id", java.util.UUID.class) - for uuids
//        if (StringUtils.containsIgnoreCase(colType, CHAR.getName())) {
//            return String.class;
//        } else if (StringUtils.containsIgnoreCase(colType, BOOLEAN.getName())) {
//            return Boolean.class;
//        } else if (StringUtils.containsIgnoreCase(colType, INTEGER.getName())) {
//            return Integer.class;
//        } else if (StringUtils.containsIgnoreCase(colType, BIGINT.getName())) {
//            return Long.class;
//        } else if (StringUtils.containsIgnoreCase(colType, REAL.getName())) {
//            return Float.class;
//        } else if (StringUtils.containsIgnoreCase(colType, DOUBLE.getName())) {
//            return Double.class;
//        } else if (StringUtils.containsIgnoreCase(colType, DATE.getName())) {
//            return LocalDate.class;
//        } else if (StringUtils.containsIgnoreCase(colType, TIME.getName())) {
//            return LocalTime.class;
//        } else if (StringUtils.containsIgnoreCase(colType, TIMESTAMP.getName())) {
//            return LocalDateTime.class;
//        } else if (StringUtils.containsIgnoreCase(colType, "UUID")) {
//            return UUID.class;
//        } else {
//            throw new RuntimeException(String.format("Type %s is not supported", colType));
//        }
//    }
//
//    private void callJdbcMethodByType(Class<?> javaType) {
//    }

    @Override
    public DbType getType() {
        return OLAP;
    }
}
