package com.dmasso.processing;

import com.dmasso.data.BaseQueryExecutor;
import com.dmasso.routing.BaseRouter;
import com.dmasso.routing.enums.DbType;
import com.dmasso.translation.BaseTranslator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CypherQueryProcessor<QT> implements QueryProcessor<String, BaseTranslator<String, QT>, Iterable<?>> {
    private final BaseRouter<String> router;
    private final Map<DbType, BaseTranslator<String, ? extends QT>> typeTranslatorMap;
    private final Map<DbType, BaseQueryExecutor<QT, ? extends Iterable<?>>> typeExecutorMap;


    @Override
    public Iterable<?> execute(String query) {
        DbType dbType = router.route(query);
        BaseTranslator<String, ? extends QT> suitableTranslator = typeTranslatorMap.get(dbType);
        QT preparedCommand = suitableTranslator.translate(query);
        BaseQueryExecutor<QT, ? extends Iterable<?>> suitableExecutor = typeExecutorMap.get(dbType);
        return suitableExecutor.execute(preparedCommand);
    }
}
