package com.dmasso.multidwh.translation.concrete.fromtext;

import com.dmasso.multidwh.common.enums.DbType;
import com.dmasso.multidwh.translation.Translator;
import org.springframework.stereotype.Component;

@Component
public class NoOpTranslator implements Translator {
    @Override
    public String translate(String query) {
        return query;
    }

    @Override
    public DbType getType() {
        return DbType.GRAPH;
    }
}
