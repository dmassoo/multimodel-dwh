package com.dmasso.routing;

import com.dmasso.routing.enums.DbType;
import org.springframework.stereotype.Component;

/**
 * Proxy that supposed to analyze input query in Cypher and decide on which DBMS it should be run
 */
@Component
public class CypherStringQueryRouter implements BaseRouter<String> {
    @Override
    public DbType route(String query) {
        // TODO: 20.01.2023 actual implementation required later
        return DbType.GRAPH;
    }
}
