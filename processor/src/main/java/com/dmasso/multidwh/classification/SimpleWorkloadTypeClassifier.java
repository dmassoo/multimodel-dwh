package com.dmasso.multidwh.classification;

import com.dmasso.multidwh.common.enums.DbType;
import org.apache.commons.lang.NotImplementedException;

import java.util.List;

public class SimpleWorkloadTypeClassifier implements Classifier<String, DbType> {
    private static final List<String> KEY_VALUE_QUERY_PATTERNS =
            List.of("MATCH (e: entityName {entityAttribute: value}) RETURN e",
                    "MATCH (e: entityName) WHERE e.entityAttribute = value RETURN e");


    @Override
    public DbType classify(String query) {
        if (isKeyValue(query)) return DbType.KEY_VALUE;
        if (isGraph(query)) return DbType.GRAPH;
        if (isOlap(query)) return DbType.OLAP;
        if (isOltp(query)) return DbType.OLTP;
        throw new RuntimeException("Classification error. Cannot classify query: " + query);
    }

    private boolean isGraph(String query) {
        throw new NotImplementedException();
    }

    private boolean isKeyValue(String query) {
        throw new NotImplementedException();
    }

    private boolean isOlap(String query) {
        throw new NotImplementedException();
    }

    private boolean isOltp(String query) {
        throw new NotImplementedException();
    }

    private String strip(String query) {
        // format query in one line for the sake of processing simplicity during classification
        throw new NotImplementedException();
    }
}
