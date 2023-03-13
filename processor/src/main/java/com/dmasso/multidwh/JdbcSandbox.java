package com.dmasso.multidwh;

import com.dmasso.multidwh.data.OltpConnectionProperties;
import com.dmasso.multidwh.execution.SqlQueryExecutor;
import org.jdbi.v3.core.Jdbi;

public class JdbcSandbox {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "password";
        Jdbi jdbi = Jdbi.create(url, user, password);
        //map: field to value
//        List<Map<String, Object>> maps = jdbi.withHandle(handle -> {
//            handle.execute("CREATE TABLE \"user\" (id INTEGER PRIMARY KEY, \"name\" VARCHAR)");
//            int execute = handle.createUpdate("INSERT INTO \"user\" (id, \"name\") VALUES (:id, :name)")
//                    .bind("id", 1)
//                    .bind("name", "Clarice")
//                    .execute();
//            //map: field to value
//            return null;
//        });

        SqlQueryExecutor sqlQueryExecutor = new SqlQueryExecutor(new OltpConnectionProperties());
        Iterable<?> execute = sqlQueryExecutor.execute("select * from \"user\"");
        System.out.println(execute);
    }
}
