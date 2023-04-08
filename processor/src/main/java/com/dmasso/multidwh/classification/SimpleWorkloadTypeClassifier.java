package com.dmasso.multidwh.classification;

import com.dmasso.multidwh.common.Constants;
import com.dmasso.multidwh.common.enums.DbType;
import com.dmasso.multidwh.metadata.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SimpleWorkloadTypeClassifier implements Classifier<String, DbType> {

    public static final String TYPE_REGEX = "\\(\\W*:(.*?)\\)";
    public static final String PREDICATE_PATTERN = ".*\s?=\s?.*";
    private static final String OLAP = "OLAP";
    private static final String ROW = "ROW";
    private static final Set<String> AGGREGATING_FUNCTIONS =
            Set.of("count(", "collect(", "sum(", "percentileDisc(", "percentileCont(", "stDev(", "stDevP(");
    private static final Set<String> STRING_MATCHING =
            Set.of(
                    // "STARTS WITH", it still can be oltp
                    "ENDS WITH",
                    "CONTAINS"
            );
    private static final int MUCH_GREATER_THRESHOLD = 10;

    private static final Set<String> OLAP_LIKE_COMPARISON_SIGNS =
            Set.of("<>", " > ", " < ", " >= ", " <= ", "IS NULL", "IS NOT NULL");

    private static final Set<String> GRAPH_PATH_RELATIONSHIP_FUNCTIONS =
            Set.of("length(", "nodes(", "relationships(", "count{", "shortestPath(", "allShortestPaths(");

    private static final Metadata metadata = MetadataUtility.readMetadata();

    public SimpleWorkloadTypeClassifier() {
    }

    @Override
    public DbType classify(String query) {
        // TODO: 04.03.2023 can be removed for end2end since prepared in CypherQueryProcessor
        query = prepareForClassification(query);
        validateQueryEntitiesExistence(query);
        if (isKeyValue(query)) return DbType.KEY_VALUE;
        if (isGraph(query)) return DbType.GRAPH;
        return classifyOlapOltp(query);
    }

    private void validateQueryEntitiesExistence(String query) {
        Map<String, String> aliasesToTypes = getAliasesToTypes(query);
        Collection<String> queryTypes = new HashSet<>(aliasesToTypes.values());
        var existingTypes = metadata.getEntities().stream()
                .flatMap(map -> map.keySet().stream())
                .collect(Collectors.toSet());
        if (!existingTypes.containsAll(queryTypes)) {
            throw new IllegalArgumentException("Unknown entity type reference");
        }
    }

    private boolean isGraph(String query) {
        if (query.contains("*") || GRAPH_PATH_RELATIONSHIP_FUNCTIONS.stream().anyMatch(query::contains)) {
            // variable length path, path functions, relationships (i.e. focus on paths and relationships
            return true;
        }
        if (containsSelfJoins(query)) {
            return true;
        }
        if (severalRelationships(query)) {
            return true;
        }
        return complexPathPattern(query);
    }

    private boolean containsSelfJoins(String query) {
        String typePattern = "\\(.*?:(.*?).*?\\)";
        Pattern compile = Pattern.compile(typePattern);
        Matcher matcher = compile.matcher(query);
        List<String> types = new ArrayList<>();
        while (matcher.find()) {
            types.add(matcher.group(1));
        }
        return types.size() != 0 && Set.of(types).size() != types.size();
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
        for (String pattern : Constants.prepareKeyValuePatterns()) {
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
        }
        return false;
    }


    private DbType classifyOlapOltp(String query) {
        if (isOlap(query)) {
            return DbType.OLAP;
        }
        // TODO: 21.02.2023 check indexed cols in predicates
        return dbTypeByIndexedPredicates(query);
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
        if (isLowEqualsPredicateCardinality(query)) {
            return true;
        }
        return fewColumnsReturnColumnOriented(query);
    }

    private boolean isLowEqualsPredicateCardinality(String query) {
        Map<String, String> aliasesToTypes = getAliasesToTypes(query);
        Map<String, Set<String>> typesToAttributes = getTypesToPredicateAttributes(query, aliasesToTypes);

        List<Integer> bigRelativeCardinalities = typesToAttributes.entrySet().stream()
                .flatMap(e -> {
                    String type = e.getKey();
                    Set<String> attrs = e.getValue();
                    Map<String, Entity> nameToEntity = getNameToEntity(type);
                    //todo fix class cast exception
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
        return metadata.getEntities().stream()
                .filter(it -> it.containsKey(type)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Запрошенный тип объекта не определен: " + type));
    }

    private Map<String, String> getAliasesToTypes(String query) {
        Pattern typePattern = Pattern.compile(TYPE_REGEX);
        Matcher typeMatcher = typePattern.matcher(query);
        var aliasesTypes = new ArrayList<String>();
        while (typeMatcher.find()) {
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
                    while (typeMatcher.find()) {
                        typesToPredicateAttributes
                                .computeIfAbsent(aliasesToTypes.get(alias), attributes -> new HashSet<>())
                                .add(typeMatcher.group(1));
                    }
                });
        return typesToPredicateAttributes;
    }

    private boolean fewColumnsReturnColumnOriented(String query) {
        Map<String, String> aliasesToTypes = getAliasesToTypes(query);
        String type;
        if (aliasesToTypes.values().size() == 1) {
            type = aliasesToTypes.values().stream().findFirst().get();
            Map<String, Entity> nameToEntity = getNameToEntity(type);
            Entity entity = nameToEntity.get(type);
            Optional<Engine> olapEngineOpt = entity.getEngines().stream().filter(e -> e.getType().equals(OLAP)).findFirst();
            if (olapEngineOpt.isPresent()) {
                Engine engine = olapEngineOpt.get();
                if (engine.getOrientation().equals(ROW)) {
                    return false;
                }
            }
            int attrsNumber = entity.getSchema().size();
            String[] split = query.split("RETURN|return");
            int length = split.length;
            String partAfterReturn = split[length - 1];
            String attrsReturnPattern = "\\w*\\.(\\w*)";
            Pattern compile = Pattern.compile(attrsReturnPattern);
            Matcher matcher = compile.matcher(partAfterReturn);
            List<String> attrNames = new ArrayList<>();
            while (matcher.find()) {
                String attrName = matcher.group(1);
                attrNames.add(attrName);
            }
            return attrsNumber / attrNames.size() <= 4;

        }

        return false;
    }

    private DbType dbTypeByIndexedPredicates(String query) {
        Pattern pattern = Pattern.compile(PREDICATE_PATTERN);
        Matcher matcher = pattern.matcher(query);
        List<String> predicates = new ArrayList<>();
        while (matcher.find()) {
            predicates.add(matcher.group(0));
        }
        List<String[]> aliasDotAttrsInPredicate = predicates.stream()
                .map(p -> p.replace(" ", ""))
                .map(p -> p.split("=")[0].split("\\."))
                .toList();
        Map<String, String> aliasesToTypes = getAliasesToTypes(query);
        Map<String, Set<String>> typesToAttributes = getTypesToPredicateAttributes(query, aliasesToTypes);
        // TODO: 28.02.2023 merge types-attributes in predicates by alias
        // 1) DBType
        // 2) Types
        // 3) Aliases
        // 4) Attributes in predicates
        // 5) check indices in metadata
        // TODO: 21.02.2023 implement
        // method to check if predicate field is indexed in certain database
        return DbType.OLTP;
    }

    private String prepareForClassification(String query) {
        return query.strip().replace("\n", " ").replace("  ", " ");
    }

    public static void main(String[] args) {
        SimpleWorkloadTypeClassifier simpleWorkloadTypeClassifier = new SimpleWorkloadTypeClassifier();

        String query = "MATCH (n:Label)-[r:(R1|Rel2)&Rel3]→(m:Label) RETURN r.name AS name";
        query = simpleWorkloadTypeClassifier.prepareForClassification(query);
        System.out.println(simpleWorkloadTypeClassifier.severalRelationships(query));


        System.out.println("(Ab: sd)".replaceAll("[() ]", ""));
    }
}
