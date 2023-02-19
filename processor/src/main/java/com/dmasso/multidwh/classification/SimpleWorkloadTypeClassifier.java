package com.dmasso.multidwh.classification;

import com.dmasso.multidwh.common.enums.DbType;
import org.apache.commons.lang.NotImplementedException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SimpleWorkloadTypeClassifier implements Classifier<String, DbType> {
    private static final Set<String> KEY_VALUE_QUERY_PATTERNS =
            new HashSet<>(Set.of("MATCH \\(e:entityName \\{entityAttribute: value\\}\\) RETURN e",
                    "MATCH \\(e:entityName\\) WHERE e.entityAttribute = value RETURN e"));
    private static final Set<String> AGGREGATING_FUNCTIONS =
            Set.of("count(", "collect(", "sum(", "percentileDisc(", "percentileCont(", "stDev(", "stDevP(");
    private static final Set<String> STRING_MATCHING = Set.of("STARTS WITH", "ENDS WITH", "CONTAINS");
    private static final Set<String> COMPARISONS = Set.of("=", "<>", "<", ">", "<=", ">=", "IS NULL", "IS NOT NULL");

    private static final Set<String> GRAPH_PATH_RELATIONSHIP_FUNCTIONS =
            Set.of("length(", "nodes(", "relationships(", "count{", "shortestPath(", "allShortestPaths(");


    public SimpleWorkloadTypeClassifier() {
        prepareKeyValuePatterns();
    }

    @Override
    public DbType classify(String query) {
        query = prepareForClassification(query);
        if (isKeyValue(query)) return DbType.KEY_VALUE;
        if (isGraph(query)) return DbType.GRAPH;
        if (isOlap(query)) return DbType.OLAP;
        if (isOltp(query)) return DbType.OLTP;
        // TODO: 19.02.2023 remove exception later and classify as OLTP for example (or graph)
        throw new RuntimeException("Classification error. Cannot classify query: " + query);
    }

    private boolean isGraph(String query) {
        if (query.contains("*") || GRAPH_PATH_RELATIONSHIP_FUNCTIONS.stream().anyMatch(query::contains)) {
            // variable length path, path functions, relationships (i.e. focus on paths and relationships
            return true;
        }
        if(containsSelfJoins(query)) {
            return true;
        }
        if (severalRelationships(query)) {
            return true;
        }
        return complexPathPattern(query);
    }

    private boolean containsSelfJoins(String  query) {
        String typePattern = "\\(.:(.*?).*?\\)";
        Pattern compile = Pattern.compile(typePattern);
        Matcher matcher = compile.matcher(query);
        List<String> types = new ArrayList<>();
        while (matcher.find()) {
            types.add(matcher.group(1));
        }
        return Set.of(types).size() != types.size();
    }

    private boolean severalRelationships(String query) {
        String relationships = "\\[.:.*?(!.*?|.*?[|&].*?)]";
        Pattern pattern = Pattern.compile(relationships);
        Matcher matcher = pattern.matcher(query);
        return matcher.find();
    }

    private boolean complexPathPattern(String query) {
        String relationshipRegex = "\\[.:.*]";
        String typeRegex = "\\(.:(.*?).*?\\)";
        Pattern relationshipPattern = Pattern.compile(relationshipRegex);
        Matcher relationshipMatcher = relationshipPattern.matcher(query);
        Pattern typePattern = Pattern.compile(typeRegex);
        Matcher typeMatcher = typePattern.matcher(query);
        int relationships = 0;
        int entities = 0;
        while (relationshipMatcher.find()) {
            relationships++;
        }
        while (typeMatcher.find()) {
            entities++;
        }
        // path pattern is considered complex if there is more than one relationship block in the pattern
        return relationships > 1 && entities > 2;
    }

    private boolean isKeyValue(String query) {
        // matches k-v pattern
        String matchingPattern = null;
        for (String pattern : KEY_VALUE_QUERY_PATTERNS) {
            if (query.matches(pattern)) matchingPattern = pattern;
        }
        if (matchingPattern != null) {
            Pattern regex = Pattern.compile(matchingPattern);
            Matcher matcher = regex.matcher(query);
            matcher.find(); // it is required to run matcher engine
            String entityName = matcher.group(1);
            String attribute = matcher.group(2);
            String value = matcher.group(3);
            return true;
            // TODO: 05.02.2023 check the metadata whether the cache for this entity attribute as a key is present (in case of not full redundancy)
        }
        return false;
    }

    private boolean isOlap(String query) {
        throw new NotImplementedException();
    }

    private boolean isOltp(String query) {
        // джойны могут быть проблемой
        // точно будут при трансляции, но при классификации тоже могут
        throw new NotImplementedException();
    }

    private String prepareForClassification(String query) {
        return query.strip().replace("\n", " ").replace("  ", " ");
    }

    private void prepareKeyValuePatterns() {
        Set<String> preparedPatterns = KEY_VALUE_QUERY_PATTERNS.stream()
                .map(s -> s.replace("entityName", "(.*?)")
                        .replace("entityAttribute", "(.*?)").replace("value", "(.*?)"))
                .collect(Collectors.toSet());
        KEY_VALUE_QUERY_PATTERNS.clear();
        KEY_VALUE_QUERY_PATTERNS.addAll(preparedPatterns);
    }

    public static void main(String[] args) {
        SimpleWorkloadTypeClassifier simpleWorkloadTypeClassifier = new SimpleWorkloadTypeClassifier();

        String query = "MATCH (n:Label)-[r:(R1|Rel2)&Rel3]→(m:Label) RETURN r.name AS name";
        query = simpleWorkloadTypeClassifier.prepareForClassification(query);
        System.out.println(simpleWorkloadTypeClassifier.severalRelationships(query));
    }
}
