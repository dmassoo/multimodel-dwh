package com.dmasso.routing;

import org.apache.commons.lang3.NotImplementedException;

/**
 * Proxy that supposed to analyze input query in Cypher and decide on which DBMS it should be run
 */
public class CypherStringQueryRouter implements Router<String> {
    @Override
    public DbType route(String query) {
        // TODO: 20.01.2023 actual implementation required later
        return DbType.GRAPH;
    }
}
