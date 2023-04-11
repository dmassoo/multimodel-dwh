// KV, uses metadata on caches
MATCH (m:movie) WHERE m.id = 654321 RETURN m;
//OR MATCH (m:movie {id: 654321}) RETURN m;
// This data is not in cache -> while it looks like kv, it is classified as OLTP
MATCH (p:person) WHERE p.id = 654321 RETURN p;

// Graph
// 1) Several relationships
MATCH (p:person)-[r:ACTED_IN|DIRECTED|PRODUCED]-(m:movie) WHERE p.id = 55321
RETURN m
// 2) Self-join and variable length path
MATCH pth = (p:person)-[*1..5]-(pe:person)
RETURN pth AS path
//not relevant for datamodel but helpful for debug
//MATCH (p:person)-[:KNOWS]-(pe:person) WHERE pe.id = 1 RETURN p
// 3) Complex path ?? model is too simple

//OLTP
MATCH (m:movie)
WHERE m.title STARTS WITH 'Love'
RETURN m

//OLAP
// 1) Aggregation
MATCH (m:movie)
WHERE m.title CONTAINS 'Love' RETURN count(*) as cnt
// no joins in OLAP
//MATCH (p:person)-[:DIRECTED]-(m:movie)
//WHERE p.name = 'Quentin Tarantino' RETURN count(*) as cnt
// 2) Scan and few columns
//MATCH (m:movie) WHERE m.released > 1977 AND m.tagline CONTAINS 'Love' RETURN m.title
// low cardinality (released)
MATCH (m:movie)
WHERE m.released = 1977 AND m.title STARTS WITH 'Love'
RETURN m
// 3) Few columns, scan
MATCH (m:movie) WHERE m.released > 2000 RETURN m.title







MATCH (m:movie)
  WHERE m.released = 1977
RETURN m