package com.dmasso.translator;

import org.cytosm.cypher2sql.PassAvailables;

public class Translator {

   public String translate(String cypherQuery, DestinationLanguage destinationLanguage) {
      return switch (destinationLanguage) {
//          case SQL -> PassAvailables.cypher2sql(null, cypherQuery);
//          case MONGO -> {
//              var sql = PassAvailables.cypher2sql(null, cypherQuery);
////              QueryConverter queryConverter = new QueryConverter.Builder().sqlString("select column1 from my_table where value NOT IN ("theValue1","theValue2","theValue3")").build();
////              MongoDBQueryHolder mongoDBQueryHolder = queryConverter.getMongoQuery();
////              String collection = mongoDBQueryHolder.getCollection();
////              Document query = mongoDBQueryHolder.getQuery();
////              Document projection = mongoDBQueryHolder.getProjection();
////              Document sort = mongoDBQueryHolder.getSort();
//          }
          default -> null;
      };
   }
}
