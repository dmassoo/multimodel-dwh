package com.dmasso.translator;

import com.dmasso.translator.specific.GraphToKeyValueTranslator;
import com.dmasso.translator.specific.CypherToRedisTranslator;
import com.github.vincentrussell.query.mongodb.sql.converter.ParseException;
import com.github.vincentrussell.query.mongodb.sql.converter.QueryConverter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.cytosm.common.gtop.GTopInterfaceImpl;
import org.cytosm.common.gtop.RelationalGTopInterface;
import org.cytosm.cypher2sql.PassAvailables;
import org.cytosm.cypher2sql.lowering.exceptions.Cypher2SqlException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Translator {
    public static void main(String[] args) throws IOException {
        String cypher = "" +
                "MATCH (a:Person) WHERE a.firstName IN ['foo', 'bar']\n" +
                "RETURN a.firstName";
        String cypher2 = """
                MATCH (a:Person) WHERE a.firstName IN ['foo', 'bar']
                RETURN a""";
        Translator translator = new Translator();
        String translate = translator.translate(cypher2, DestinationLanguage.MONGO);
        System.out.println(translate);
    }

    // TODO: 19.01.2023 Make it configurable from outside
    public static String getPathToGtop() {
        return "C:\\Users\\Dmitrii\\Desktop\\BIG_DATA\\__3sem_Practice\\multimodel-dwh\\translator\\src\\main\\resources\\movies.gtop";
    }

    private GraphToKeyValueTranslator toKeyValueTranslator = new CypherToRedisTranslator();

    public String translate(String cypherQuery, DestinationLanguage destinationLanguage) {
        GTopInterfaceImpl gTopInterface;
        try {
            gTopInterface = getGTopInterface(getPathToGtop());
        } catch (IOException e) {
            throw new RuntimeException("No gTop file provided", e);
        }

        return switch (destinationLanguage) {
            case SQL -> tryTranslateCypherToSql(gTopInterface, cypherQuery);
            case MONGO -> {
                String sqlQuery = tryTranslateCypherToSql(gTopInterface, cypherQuery);
                yield tryTranslateSqlToMongo(sqlQuery);
            }
            case REDIS -> toKeyValueTranslator.translate(cypherQuery);
            // TODO: 20.01.2023 https://neo4j.com/developer/elastic-search/
            case ELASTIC -> throw new NotImplementedException("ElasticSearch is Extra Goal");
        };

    }

    private static String tryTranslateSqlToMongo(String sqlQuery) {
        try {
            QueryConverter queryConverter = new QueryConverter.Builder().sqlString(sqlQuery).build();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            queryConverter.write(byteArrayOutputStream);
            return byteArrayOutputStream.toString();
        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String tryTranslateCypherToSql(GTopInterfaceImpl gTopInterface, String cypherQuery) {
        try {
            return PassAvailables.cypher2sqlOnExpandedPaths(gTopInterface, cypherQuery).toSQLString();
        } catch (Cypher2SqlException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static GTopInterfaceImpl getGTopInterface(String pathToGtop) throws IOException {
        String jsonInString = FileUtils.readFileToString(new File(pathToGtop));
        return new RelationalGTopInterface(jsonInString);
    }
}
