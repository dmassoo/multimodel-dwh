package com.dmasso.multidwh.routing;

import com.dmasso.multidwh.common.enums.DbType;
import org.apache.commons.lang.NotImplementedException;
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

    /**
     * If query is just a value lookup by key like:
     *  MATCH (a:Person)
     *  WHERE a.firstName = 'John'
     *  RETURN a;
     * @param query query
     * @return true if query matches key-value pattern
     */
    private boolean checkKeyValue(String query) {
        throw new NotImplementedException();
    }


}
