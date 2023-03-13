package com.dmasso.multidwh.execution;

import com.dmasso.multidwh.common.enums.DbType;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.springframework.beans.factory.annotation.Value;

//@Component
public class CypherQueryExecutor implements QueryExecutor<String> {
    private final Driver driver;

    public CypherQueryExecutor(@Value("${neo4j.uri}") String uri) {
        this.driver = GraphDatabase.driver(uri);
    }

    @Override
    public DbType getType() {
        return DbType.GRAPH;
    }

    @Override
    public Iterable<?> execute(String query) {
        Iterable<?> result;
        try (var session = driver.session()) {
                result = session.executeWrite(tx -> {
                var _query = new Query(query);
                var _result = tx.run(_query);
                return _result.list();
            });
        }
        return result;
    }
}
