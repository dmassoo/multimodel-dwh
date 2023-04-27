INSERT INTO movie
FROM INFILE '/csv/movie_10m.csv'
FORMAT CSV;