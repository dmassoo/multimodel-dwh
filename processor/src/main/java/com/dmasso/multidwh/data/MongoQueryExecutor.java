package com.dmasso.multidwh.data;

import com.dmasso.multidwh.common.enums.DbType;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;


@Deprecated(since = "document db is out of scope now")
//@Component
@RequiredArgsConstructor
public class MongoQueryExecutor implements QueryExecutor<String> {

    @Value("${mongo.uri}")
    private final String mongoUri;

    @Override
    public Iterable<?> execute(String query) {
        Iterable<?> result = null;
        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
            try {
                // TODO: 21.01.2023 check it. probably find could match. See also https://www.mongodb.com/community/forums/t/native-query-execution-in-java/6602/3 
                Document commandResult = database.runCommand(Document.parse(query));
                result = List.of(commandResult);
                System.out.println("dbStats: " + commandResult.toJson());
            } catch (MongoException me) {
                System.err.println("An error occurred: " + me);
            }
        }
        return result;
    }

    @Override
    public DbType getType() {
        return DbType.DOCUMENT;
    }
}
