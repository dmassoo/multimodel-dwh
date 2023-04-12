package com.dmasso.multidwh.classification;

import com.dmasso.multidwh.common.enums.DbType;
import org.junit.Test;

public class ClassificationTest {
    private static final Classifier<String, DbType> classifier = new SimpleWorkloadTypeClassifier();


    @Test
    public void testRelationshipOrientedQuery_returnsGraph() {
        String query = """
                MATCH (n:person)-[r]->(m:person)
                WHERE r:R1|R2
                RETURN r.name AS name
                """;
        DbType result = classifier.classify(query);
        assert result == DbType.GRAPH;
    }

    @Test
    public void testSeveralRelationships_returnsGraph() {
        String query = """
                MATCH (p:person)-[r:ACTED_IN|DIRECTED|PRODUCED]->(m:movie) WHERE p.id = 55321
                RETURN m
                """;
        DbType result = classifier.classify(query);
        assert result == DbType.GRAPH;
    }

    @Test
    public void testVariableLength_returnsGraph() {
        var query = """
                MATCH pth = (p:person)-[*1..5]-(pe:person)
                RETURN pth AS path
                """;
        DbType result = classifier.classify(query);
        assert result == DbType.GRAPH;
    }

    @Test
    public void testKVPattern_returnsKV() {
        var query = "MATCH (m:movie) WHERE m.id = 654321 RETURN m;";
        DbType result = classifier.classify(query);
        assert result == DbType.KEY_VALUE;
    }

    @Test
    public void testKVPattern_noKVMetadata() {
        var query = "MATCH (p:person) WHERE p.id = 654321 RETURN p;";
        DbType result = classifier.classify(query);
        assert result == DbType.OLTP;
    }

    @Test
    public void testOLAP_Scan() {
        var query = "MATCH (m:movie) WHERE m.title CONTAINS 'Love' RETURN m.id";
        DbType result = classifier.classify(query);
        assert result == DbType.OLAP;
    }

    @Test
    public void testOLAPAgg() {
        var query = """
                MATCH (p:person)-[:DIRECTED]->(m:movie)
                WHERE p.name = 'Quentin Tarantino' RETURN count(*) as cnt
                """;
        DbType result = classifier.classify(query);
        assert result == DbType.OLAP;
    }

    @Test
    public void testOLAP_LowCard() {
        var query = """
                MATCH (m:movie)
                WHERE m.released = 1977
                RETURN m.id, m.released, m.title, m.tagline
                """;
        DbType result = classifier.classify(query);
        assert result == DbType.OLAP;
    }

    @Test
    public void testOLAP_fewColsSelectInColOriented() {
        var query = "MATCH (m:movie) WHERE m.released > 2000 RETURN m.title";
        DbType result = classifier.classify(query);
        assert result == DbType.OLAP;
    }

    @Test
    public void testCompareIndices() {
        var query = """
                MATCH (m:movie)
                WHERE m.title = 'Spider Man' and m.released = 2000
                RETURN m.id, m.released, m.title, m.tagline
                """;
        DbType result = classifier.classify(query);
        assert result == DbType.OLTP;
    }

    @Test
    public void testOLTP() {
        var query = """
                MATCH (m:movie)
                WHERE m.title STARTS WITH 'Love'
                RETURN m.id, m.released, m.title, m.tagline
                """;
        DbType result = classifier.classify(query);
        assert result == DbType.OLTP;
    }
}
