package com.dmasso.multidwh.processing;

import com.dmasso.multidwh.common.enums.DbType;
import com.dmasso.multidwh.common.interfaces.DbTyped;
import com.dmasso.multidwh.data.QueryExecutor;
import com.dmasso.multidwh.routing.BaseRouter;
import com.dmasso.multidwh.translation.BaseTranslator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Service
@RequiredArgsConstructor
public class CypherQueryProcessor implements QueryProcessor<String, Iterable<?>> {
    private final BaseRouter<String> router;
    private final List<BaseTranslator<String, ?>> translators;
    private final List<QueryExecutor<? super Object>> executors;
    private Map<DbType, BaseTranslator<String, ?>> typeTranslatorMap;
    private Map<DbType, QueryExecutor<? super Object>> typeExecutorMap;


    @Override
    public Iterable<?> execute(String query) {
        DbType dbType = router.route(query);

        BaseTranslator<String, ?> suitableTranslator = typeTranslatorMap.get(dbType);
        QueryExecutor<Object> suitableExecutor = typeExecutorMap.get(dbType);
        // TODO: 22.01.2023 the problem with this is non-unified representation interface.
        // this fact leads to problems with view/representation of these results for end client
        return suitableExecutor.execute(suitableTranslator.translate(query));
    }

    @PostConstruct
    void setMaps() {
        typeTranslatorMap = translators.stream()
                .collect(Collectors.toUnmodifiableMap(DbTyped::getType, identity()));
        typeExecutorMap = executors.stream()
                .collect(Collectors.toUnmodifiableMap(DbTyped::getType, Function.identity()));
    }
}
