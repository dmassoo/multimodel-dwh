package com.dmasso.multidwh.execution;

import com.dmasso.multidwh.common.enums.DbType;
import com.dmasso.multidwh.data.Neo4JConnectionProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.PathValue;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CypherQueryExecutor implements QueryExecutor<String> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final Driver driver;


    public CypherQueryExecutor(Neo4JConnectionProperties connectionProperties) {
        this.driver = GraphDatabase.driver(connectionProperties.getUri());
    }

    @Override
    public Iterable<?> execute(String query) {
        List<? super Object> result = new ArrayList<>();
        try (var session = driver.session()) {
            session.executeRead(tx -> {
                var _query = new Query(query);
                var _result = tx.run(_query);
                var r = _result.list().stream()
                        .map(o -> {
                                    if (o.values().get(0) instanceof PathValue) {
                                        return o.values().stream()
                                                .map(Value::asPath)
                                                .map(Object::toString)
                                                .toList();
                                    } else if (o.values().get(0) instanceof NodeValue) {
                                        try {
                                            return OBJECT_MAPPER.writeValueAsString(o.values().stream()
                                                    .map(Value::asNode)
                                                    .map(Node::asMap)
                                                    .toList());
                                        } catch (JsonProcessingException e) {
                                            return null;
                                        }
                                    } else {
                                        try {
                                            return OBJECT_MAPPER.writeValueAsString(o.values().stream()
                                                    .map(Value::asObject)
                                                    .toList());
                                        } catch (JsonProcessingException e) {
                                            return null;
                                        }
                                    }
                                }
                        )
                        .toList();

                result.addAll(r);
                return result;
            });
        }
        return result;
    }

    @Override
    public DbType getType() {
        return DbType.GRAPH;
    }
}
