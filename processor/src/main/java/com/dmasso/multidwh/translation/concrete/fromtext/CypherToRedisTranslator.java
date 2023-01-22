package com.dmasso.multidwh.translation.concrete.fromtext;

import com.dmasso.multidwh.common.enums.DbType;
import com.dmasso.multidwh.translation.Preparer;
import org.apache.commons.lang.NotImplementedException;

import static com.dmasso.multidwh.common.enums.DbType.KEY_VALUE;

/**
 * This translator works in assumption that Redis is target storage in case of simple key-value lookup, i.e.
 * it supposed to work with queries like:
 *                 MATCH (a:Person) WHERE a.firstName = 'John'
 *                 RETURN a;
 * Predicate can be in the form of single value, label of 'a' is namespace, firstName property should be the key
 */
public class CypherToRedisTranslator implements Preparer<String> {
    @Override
    public String translate(String cypherQuery) {
        // TODO: 20.01.2023 find if I can query in CLI lang or should use clients
        throw new NotImplementedException();
    }

    @Override
    public DbType getType() {
        return KEY_VALUE;
    }
}
