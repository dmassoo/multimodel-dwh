MATCH (a:person) WITH a AS b MATCH (b)-[:KNOWS]-(c:person) WHERE c.firstName = b.firstName RETURN b.firstName, c.age
MATCH (a:person)-[:ACTED_IN]-(m:movie) WHERE m.released = 1999 RETURN a
MATCH (m:movie) WHERE m.released = 1999 AND m.id = 1 RETURN m
MATCH (m:movie) WHERE m.released = 1999 AND m.id = 1 RETURN m.id, m.released, m.title, m.tagline

// k-v query
MATCH (m:movie) WHERE m.id = 1 RETURN m




CREATE (n:movie {id: 1, title: 'Title', release: 1999})

//test for Neo4j
CREATE (m:movie)-[:Related]-(n:movie)
MATCH (m:movie) RETURN m
MATCH (m:movie) RETURN m.title
MATCH (n:movie)-[:Related]-(m:movie) return n, m
MATCH p = (n:movie)--(m:movie) RETURN p AS path