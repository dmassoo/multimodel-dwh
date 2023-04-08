package com.dmasso.multidwh.translation.concrete.fromtext;

import com.dmasso.multidwh.common.enums.DbType;
import com.dmasso.multidwh.translation.Preparer;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dmasso.multidwh.common.Constants.prepareKeyValuePatterns;
import static com.dmasso.multidwh.common.enums.DbType.KEY_VALUE;

/**
 * This translator works in assumption that Redis is target storage in case of simple key-value lookup, i.e.
 * it supposed to work with queries like:
 * MATCH (a:Person) WHERE a.firstName = 'John'
 * RETURN a;
 * Predicate can be in the form of single value, label of 'a' is namespace, firstName property should be the key
 */
@Component
public class CypherToRedisTranslator implements Preparer<String> {
    public CypherToRedisTranslator() {
    }

    @Override
    public String translate(String cypherQuery) {
        String matchingPattern = null;
        for (String pattern : prepareKeyValuePatterns()) {
            if (cypherQuery.matches(pattern)) matchingPattern = pattern;
        }
        assert matchingPattern != null;
        Pattern regex = Pattern.compile(matchingPattern);
        Matcher matcher = regex.matcher(cypherQuery);
        matcher.find(); // it is required to run matcher engine
        String entityName = matcher.group(1);
        String attribute = matcher.group(2);
        String value = matcher.group(3);
        return prepareKey(entityName, attribute, value);
    }

    private String prepareKey(String entityName, String attribute, String value) {
        return entityName + "/" + attribute + "/" + value;
    }

    @Override
    public DbType getType() {
        return KEY_VALUE;
    }
}
