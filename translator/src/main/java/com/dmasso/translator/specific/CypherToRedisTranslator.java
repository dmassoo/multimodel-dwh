package com.dmasso.translator.specific;

import org.apache.commons.lang.NotImplementedException;

/**
 * This translator works in assumption that Redis is target storage in case of simple key-value lookup, i.e.
 * it supposed to work with queries like:
 *                 MATCH (a:Person) WHERE a.firstName = 'John'
 *                 RETURN a""";
 * Predicate can be in the form of single value, label of 'a' is namespace, firstName property should be the key
 */
public class CypherToRedisTranslator implements GraphToKeyValueTranslator {
    @Override
    public String translate(String cypherQuery) {
        // TODO: 20.01.2023 find if I can query in CLI lang or should use clients
        // the same for mongo
        throw new NotImplementedException();
    }
}
