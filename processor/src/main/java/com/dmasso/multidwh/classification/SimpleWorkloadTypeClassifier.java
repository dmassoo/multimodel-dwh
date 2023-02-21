package com.dmasso.multidwh.classification;

import com.dmasso.multidwh.common.enums.DbType;
import com.dmasso.multidwh.metadata.Entity;
import com.dmasso.multidwh.metadata.FieldData;
import com.dmasso.multidwh.metadata.Metadata;
import com.dmasso.multidwh.metadata.MetadataUtility;
import org.apache.commons.lang.NotImplementedException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleWorkloadTypeClassifier implements Classifier<String, DbType> {
    private static final Set<String> KEY_VALUE_QUERY_PATTERNS =
            new HashSet<>(Set.of("MATCH \\(e:entityName \\{entityAttribute: value\\}\\) RETURN e",
                    "MATCH \\(e:entityName\\) WHERE e.entityAttribute = value RETURN e"));
    private static final Set<String> AGGREGATING_FUNCTIONS =
            Set.of("count(", "collect(", "sum(", "percentileDisc(", "percentileCont(", "stDev(", "stDevP(");
    private static final Set<String> STRING_MATCHING = Set.of("STARTS WITH", "ENDS WITH", "CONTAINS");
    private static final int MUCH_GREATER_THRESHOLD = 10;

    private static final Set<String> OLAP_LIKE_COMPARISON_SIGNS =
            Set.of("<>", " > ", " < ", " >= ", " <= " , "IS NULL", "IS NOT NULL");

    private static final Set<String> GRAPH_PATH_RELATIONSHIP_FUNCTIONS =
            Set.of("length(", "nodes(", "relationships(", "count{", "shortestPath(", "allShortestPaths(");

    private static final Metadata metadata = MetadataUtility.readMetadata();

    public SimpleWorkloadTypeClassifier() {
        prepareKeyValuePatterns();
    }

    @Override
    public DbType classify(String query) {
        query = prepareForClassification(query);
        if (isKeyValue(query)) return DbType.KEY_VALUE;
        if (isGraph(query)) return DbType.GRAPH;
        return classifyOlapOltp(query);
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
            // TODO: 21.02.2023 вынести логику проверки существования типов сущностей наверх (перед классификацией)
            getNameToEntity(entityName);
            return true;
            // TODO: 05.02.2023 check the metadata whether the cache for this entity attribute as a key is present (in case of not full redundancy)
        }
        return false;
    }


    private DbType classifyOlapOltp(String query) {
        if (isOlap(query)) {
            return DbType.OLAP;
        }
        // TODO: 21.02.2023 check indexed cols in predicates
        // TODO: check few columns if column oriented
        return null;
    }

    private boolean isOlap(String query) {
        if (AGGREGATING_FUNCTIONS.stream().anyMatch(query::contains)) {
            return true;
        }
        if (STRING_MATCHING.stream().anyMatch(query::contains)) {
            return true;
        }
        if (OLAP_LIKE_COMPARISON_SIGNS.stream().anyMatch(query::contains)) {
            return true;
        }
        return isLowEqualsPredicateCardinality(query);
    }

    private boolean isLowEqualsPredicateCardinality(String query) {
        Map<String, String> aliasesToTypes = getAliasesToTypes(query);
        Map<String, Set<String>> typesToAttributes =  getTypesToPredicateAttributes(query, aliasesToTypes);

        List<Integer> bigRelativeCardinalities = typesToAttributes.entrySet().stream()
                .flatMap(e -> {
                    String type = e.getKey();
                    Set<String> attrs = e.getValue();
                    Map<String, Entity> nameToEntity = getNameToEntity(type);
                    Entity entity = nameToEntity.get(type);
                    int cardinality = entity.getCardinality();
                    List<FieldData> schema = entity.getSchema();
                    Stream<FieldData> relatedFields = schema.stream()
                            .filter(field -> attrs.contains(type));
                    return relatedFields.map(c -> cardinality / c.getCardinality());
                })
                .filter(rc -> rc < MUCH_GREATER_THRESHOLD)
                .toList();
        // if all the cardinalities are relatively small (c_entity > c_attr * MUCH_GREATER_THRESHOLD
        // for all related entities and attributes) then list is empty
        return bigRelativeCardinalities.size() == 0;
    }

    private static Map<String, Entity> getNameToEntity(String type) {
        Map<String, Entity> nameToEntity = metadata.getEntities().stream()
                .filter(it -> it.containsKey(type)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Запрошенный тип объекта не определен: " + type));
        return nameToEntity;
    }

    private Map<String, String> getAliasesToTypes(String query) {
        String typeRegex = "\\(.*?:(.*?)\\)";
        Pattern typePattern = Pattern.compile(typeRegex);
        Matcher typeMatcher = typePattern.matcher(query);
        var aliasesTypes = new ArrayList<String>();
        while(typeMatcher.find()) {
            aliasesTypes.add(typeMatcher.group());
        }
        return aliasesTypes.stream()
                .map(at -> at.replaceAll("[() ]", ""))
                .map(at -> at.split(":"))
                .collect(Collectors.toMap(at -> at[0], at -> at[1]));
    }

    private Map<String, Set<String>> getTypesToPredicateAttributes(String query, Map<String, String> aliasesToTypes) {
        Map<String, Set<String>> typesToPredicateAttributes = new HashMap<>();

        aliasesToTypes.keySet()
                .forEach(alias -> {
                    String typeRegex = String.format("\\(%s.(.*?)\\)", alias);
                    Pattern typePattern = Pattern.compile(typeRegex);
                    Matcher typeMatcher = typePattern.matcher(query);
                    while(typeMatcher.find()) {
                        typesToPredicateAttributes
                                .computeIfAbsent(aliasesToTypes.get(alias), attributes -> new HashSet<>())
                                .add(typeMatcher.group(1));
                    }
                } );
        return typesToPredicateAttributes;
    }

    private boolean fewColumnsReturn(String query) {
        //todo use entity orientation metadata
        return false;
    }

    private boolean indexedPredicate(String query, DbType dbType) {
        // TODO: 21.02.2023 implement
        // method to check if predicate field is indexed in certain database
        return false;
    }

    private boolean isOltp(String query) {
        // TODO: 21.02.2023 мб, не нужен ????
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


        System.out.println("(Ab: sd)".replaceAll("[() ]", ""));
    }
}
