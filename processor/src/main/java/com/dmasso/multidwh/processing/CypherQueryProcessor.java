package com.dmasso.multidwh.processing;

import com.dmasso.multidwh.classification.Classifier;
import com.dmasso.multidwh.common.enums.DbType;
import com.dmasso.multidwh.common.interfaces.DbTyped;
import com.dmasso.multidwh.execution.QueryExecutor;
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

    private final Classifier<String, DbType> classifier;
    private final List<BaseTranslator<String, String>> translators;
    private final List<QueryExecutor<String>> executors;
    private Map<DbType, BaseTranslator<String, String>> typeTranslatorMap;
    private Map<DbType, QueryExecutor<String>> typeExecutorMap;


    @Override
    public Iterable<?> execute(String query) {
        query = prepareForProcessing(query);
        DbType dbType = classifier.classify(query);

        var suitableTranslator = typeTranslatorMap.get(dbType);
        var suitableExecutor = typeExecutorMap.get(dbType);
        String translatedQuery = suitableTranslator.translate(query);
        return suitableExecutor.execute(translatedQuery);
    }

    @PostConstruct
    void setMaps() {
        typeTranslatorMap = translators.stream()
                .collect(Collectors.toUnmodifiableMap(DbTyped::getType, identity()));
        typeExecutorMap = executors.stream()
                .collect(Collectors.toUnmodifiableMap(DbTyped::getType, Function.identity()));
    }

    private String prepareForProcessing(String query) {
        return query.strip().replace("\n", " ").replace("  ", " ");
    }
}
