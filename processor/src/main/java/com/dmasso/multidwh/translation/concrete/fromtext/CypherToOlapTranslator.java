package com.dmasso.multidwh.translation.concrete.fromtext;

import com.dmasso.multidwh.common.enums.DbType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.dmasso.multidwh.common.enums.DbType.OLAP;

@Component

public class CypherToOlapTranslator extends CypherToSqlTranslator {
    public CypherToOlapTranslator(@Value("${gtop.path}") String pathToGtop) {
        super(pathToGtop);
    }

    @Override
    public DbType getType() {
        return OLAP;
    }
}
