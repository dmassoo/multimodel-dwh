package com.dmasso.multidwh.translation.concrete.fromtext;

import com.dmasso.multidwh.common.enums.DbType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.dmasso.multidwh.common.enums.DbType.OLTP;

@Component
public class CypherToOltpTranslator extends CypherToSqlTranslator {
    public CypherToOltpTranslator(@Value("${gtop.path}") String pathToGtop) {
        super(pathToGtop);
    }

    @Override
    public DbType getType() {
        return OLTP;
    }
}
