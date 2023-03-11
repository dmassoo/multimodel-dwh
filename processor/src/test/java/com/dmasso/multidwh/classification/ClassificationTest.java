package com.dmasso.multidwh.classification;

import com.dmasso.multidwh.common.enums.DbType;
import org.junit.Test;

public class ClassificationTest {
    private static final Classifier<String, DbType> classifier = new SimpleWorkloadTypeClassifier();

    @Test
    public void testRelationshipOrientedQuery() {
        String query = """
                MATCH (n:Label)-[r]->(m:Label)
                WHERE r:R1|R2
                RETURN r.name AS name
                """;
        DbType result = classifier.classify(query);
        assert result == DbType.GRAPH;
    }

    @Test
    public void testSeveralRelationships() {
        String query = "MATCH (n:Label)-[r:(R1|Rel2)&Rel3]→(m:Label) RETURN r.name AS name";
        DbType result = classifier.classify(query);
        assert result == DbType.GRAPH;
    }

    @Test
    public void testVariableLength() {
        var query = "MATCH p = (a)-[:KNOWS*]->()\n" +
                "RETURN relationships(p) AS r";
        DbType result = classifier.classify(query);
        assert result == DbType.GRAPH;
    }


}
