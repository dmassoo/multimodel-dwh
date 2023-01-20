package com.dmasso.data;


import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Component;

@Component
public class SqlQueryExecutor implements QueryExecutor<String> {
    private Jdbi jdbi = Jdbi.create("jdbc:hsqldb:mem:testDB", "sa", "");
    @Override
    public Iterable<?> execute(String query) {
        // TODO: 21.01.2023 come up with that (how to get results from arbitrary table)
        jdbi.withHandle(handle -> {
            return handle.select(query);
        });
        return null;
    }
}
