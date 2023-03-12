package com.dmasso.multidwh;

import org.jdbi.v3.core.Jdbi;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class JdbcSandbox {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "password";
        Jdbi jdbi = Jdbi.create(url, user, password);
        //map: field to value
        List<Map<String, Object>> maps = jdbi.withHandle(handle -> {
            handle.execute("CREATE TABLE \"user\" (id INTEGER PRIMARY KEY, \"name\" VARCHAR)");
            int execute = handle.createUpdate("INSERT INTO \"user\" (id, \"name\") VALUES (:id, :name)")
                    .bind("id", 1)
                    .bind("name", "Clarice")
                    .execute();
            //map: field to value
            return null;
        });

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from \"user\"");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
        } catch (SQLException e) {

        }
    }
}
