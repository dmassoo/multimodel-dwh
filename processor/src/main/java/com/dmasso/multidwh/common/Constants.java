package com.dmasso.multidwh.common;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Constants {
    public static final Set<String> KEY_VALUE_QUERY_PATTERNS =
            new HashSet<>(Set.of("MATCH \\(alias:entityName \\{entityAttribute: value\\}\\) RETURN alias;?",
                    "MATCH \\(alias:entityName\\) WHERE alias.entityAttribute = value RETURN alias;?"));

    public static Set<String> prepareKeyValuePatterns() {
        return KEY_VALUE_QUERY_PATTERNS.stream()
                .map(s -> s.replace("entityName", "(\\w*)")
                        .replace("entityAttribute", "(\\w*)")
                        .replace("value", "(\\w*)")
                        .replace("alias", "\\w*")
                )
                .collect(Collectors.toSet());
    }
}
