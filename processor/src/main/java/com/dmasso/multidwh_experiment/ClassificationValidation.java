package com.dmasso.multidwh_experiment;

import com.dmasso.multidwh.classification.Classifier;
import com.dmasso.multidwh.classification.SimpleWorkloadTypeClassifier;
import com.dmasso.multidwh.common.enums.DbType;
import org.junit.Test;

public class ClassificationValidation {

    private static final Classifier<String, DbType> classifier = new SimpleWorkloadTypeClassifier();


    @Test
    public void testKV() {
        String query = """
                MATCH (m:movie) WHERE m.id = 654321 RETURN m;
                 """;
        DbType result = classifier.classify(query);
        assert result == DbType.KEY_VALUE;
    }

    @Test
    public void testSimpleWhereStartsWith() {
        String query = """
                MATCH (m:movie)
                WHERE m.title = 'Love'
                RETURN m.id, m.released, m.title, m.tagline
                """;
        DbType result = classifier.classify(query);
        assert result == DbType.OLTP;
    }

    @Test

    public void testRangeReadOneCol() {
        String query = """
                MATCH (m:movie) WHERE m.released > 2000 RETURN m.title
                 """;
        DbType result = classifier.classify(query);
        assert result == DbType.OLAP;
    }

    @Test
    public void testNERead() {
        String query = """
                MATCH (m:movie)
                WHERE m.title <> 'Love'
                RETURN m.id, m.released, m.title, m.tagline
                """;
        DbType result = classifier.classify(query);
        assert result == DbType.OLAP;
    }

    @Test
    public void testCompoundPredicate() {
        String query = """
                MATCH (m:movie)
                WHERE m.title = 'Spider Man' and m.released = 2000
                RETURN m.id, m.released, m.title, m.tagline
                """;
        DbType result = classifier.classify(query);
        assert result == DbType.OLTP;
    }

}
