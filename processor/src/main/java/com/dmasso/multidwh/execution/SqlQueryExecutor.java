package com.dmasso.multidwh.execution;


import com.dmasso.multidwh.common.enums.DbType;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.dmasso.multidwh.common.enums.DbType.OLAP;

@Component
public class SqlQueryExecutor implements QueryExecutor<String> {

    @Value("${jdbc.uri}")
    private String uri; // TODO: 22.01.2023 create configuration properties and pass config into Jdbi

    private Jdbi jdbi = Jdbi.create("jdbc:hsqldb:mem:testDB", "sa", "");
    @Override
    public Iterable<?> execute(String query) {
        return jdbi.withHandle(handle -> handle.select(query).mapToMap());
    }

    @Override
    public DbType getType() {
        return OLAP;
    }
}
