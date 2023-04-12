// can be rewritten for all data models
MATCH (m:movie) WHERE m.id = 654321 RETURN m;
// SELECT * from movie WHERE movie.id = 654321;
// get(movie/id/654321)

// for all except Redis
// OLTP
MATCH (m:movie)
  WHERE m.title STARTS WITH 'Love'
RETURN m.id, m.released, m.title, m.tagline
// SELECT * FROM movie WHERE title like 'Love%';


//more like OLAP (released is low card field)
MATCH (m:movie)
  WHERE m.title = 'Spider Man' and m.released = 2000
RETURN m.id, m.released, m.title, m.tagline
// SELECT * FROM movie WHERE title = 'Spider Man' and released = 2000;
//olap
MATCH (m:movie) WHERE m.released > 1977 AND m.tagline CONTAINS 'Love' RETURN m.title
// SELECT * FROM movie WHERE released > 1977 and tagline like '%Love%';

MATCH (m:movie) WHERE m.released > 2000 RETURN m.title
// SELECT * FROM movie WHERE released >