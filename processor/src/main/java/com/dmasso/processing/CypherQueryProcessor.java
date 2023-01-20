package com.dmasso.processing;

import com.dmasso.data.BaseQueryExecutor;
import com.dmasso.routing.DbType;
import com.dmasso.routing.Router;
import com.dmasso.translation.BaseTranslator;

import java.util.Map;

public class CypherQueryProcessor<QT> implements QueryProcessor<String, QT> {
    private Router<String> router;
    private Map<DbType, BaseTranslator<String, ? extends QT>> typeTranslatorMap;
    private Map<DbType, BaseQueryExecutor<QT, ? extends Iterable<?>>> typeExecutorMap;

    public Iterable<?> execute(String query) {
        DbType dbType = router.route(query);
        BaseTranslator<String, ? extends QT> suitableTranslator = typeTranslatorMap.get(dbType);
        QT preparedCommand = suitableTranslator.translate(query);
        BaseQueryExecutor<QT, ? extends Iterable<?>> suitableExecutor = typeExecutorMap.get(dbType);
        return suitableExecutor.execute(preparedCommand);
    }
}
