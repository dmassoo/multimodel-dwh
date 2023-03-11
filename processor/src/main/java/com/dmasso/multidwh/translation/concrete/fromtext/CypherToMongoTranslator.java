package com.dmasso.multidwh.translation.concrete.fromtext;

import com.dmasso.multidwh.common.enums.DbType;
import com.dmasso.multidwh.translation.Translator;
import com.github.vincentrussell.query.mongodb.sql.converter.ParseException;
import com.github.vincentrussell.query.mongodb.sql.converter.QueryConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Deprecated(since = "document db is out of scope now")
@Component
@RequiredArgsConstructor
public class CypherToMongoTranslator implements Translator {
    private final CypherToSqlTranslator cypherToSqlTranslator;


    @Override
    public String translate(String query) {
        String sqlQuery = cypherToSqlTranslator.translate(query);
        return tryTranslateSqlToMongo(sqlQuery);
    }

    private String tryTranslateSqlToMongo(String sqlQuery) {
        try {
            QueryConverter queryConverter = new QueryConverter.Builder().sqlString(sqlQuery).build();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            queryConverter.write(byteArrayOutputStream);
            return byteArrayOutputStream.toString();
        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DbType getType() {
        return DbType.DOCUMENT;
    }
}
