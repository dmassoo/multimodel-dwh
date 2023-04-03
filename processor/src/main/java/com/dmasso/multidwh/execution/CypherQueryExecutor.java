package com.dmasso.multidwh.execution;

import com.dmasso.multidwh.common.enums.DbType;
import com.dmasso.multidwh.data.Neo4JConnectionProperties;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.springframework.stereotype.Component;

@Component
public class CypherQueryExecutor implements QueryExecutor<String> {
    private final Driver driver;


    public CypherQueryExecutor(Neo4JConnectionProperties connectionProperties) {
        this.driver = GraphDatabase.driver(connectionProperties.getUri());
    }

    @Override
    public Iterable<?> execute(String query) {
        Iterable<?> result = null;
//        try (var session = driver.session()) {
//                result = session.executeWrite(tx -> {
//                var _query = new Query(query);
//                var _result = tx.run(_query);
//                return _result.list();
//            });
//        }
        try (var session = driver.session()) {
            session.executeRead(tx -> {
                var _query = new Query(query);
                var _result = tx.run(_query);
                return _result.next();
            });
        }
        return result;
    }

    @Override
    public DbType getType() {
        return DbType.GRAPH;
    }
}
