package com.dmasso.multidwh.common.enums;

public enum DbType {
    OLAP,
    OLTP, // or GRAPH is enough? check also https://github.com/DTG-FRESCO/cyp2sql
    KEY_VALUE,
    GRAPH,


    //FULL_TEXT, // neo4j integrates with lucene, redis has redisearch, mongo had text search capabilities, check these features before considering elastic!
    DOCUMENT
}
