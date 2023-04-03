create table acted_in
(
    role      varchar(100),
    person_id INTEGER,
    movie_id  INTEGER
);

create table directed
(
    person_id INTEGER,
    movie_id  INTEGER
);

create table movie
(
    id       INTEGER,
    title    VARCHAR(100),
    released INTEGER,
    tagline  VARCHAR(100)
);

create table person
(
    id   INTEGER,
    name VARCHAR(100),
    born INTEGER
);

create table produced
(
    person_id INTEGER,
    movie_id  INTEGER
);