// KV
MATCH (m:movie) WHERE m.id = 654321 RETURN m;

// Graph
// 1) Several relationships
MATCH (p:person)-[r:ACTED_IN|DIRECTED|PRODUCED]-(m:movie) WHERE p.id = 55321
RETURN m
// 2) Self-join and variable length path
MATCH pth = (p:person)-[*1..5]-(pe:person)
RETURN pth AS path
// 3) Complex path ?? model is too simple


//OLTP
MATCH (m:movie)
WHERE m.release > 1977 AND m.title STARTS WITH 'Love'
RETURN m

//OLAP
// 1) Aggregation
MATCH (m:movie)
WHERE m.title CONTAINS 'Love' RETURN count(*) as cnt
// no joins in OLAP
//MATCH (p:person)-[:DIRECTED]-(m:movie)
//WHERE p.name = 'Quentin Tarantino' RETURN count(*) as cnt
// 2) Scan
MATCH (m:movie) WHERE m.release > 1977 AND m.tagline CONTAINS 'Love' RETURN m
// 3) Few columns
MATCH (m:movie) WHERE m.release > 2000 RETURN m.title