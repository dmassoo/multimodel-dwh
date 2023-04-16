create table if not exists acted_in
(
    role      varchar(100),
    person_id INTEGER,
    movie_id  INTEGER
);

create table if not exists directed
(
    person_id INTEGER,
    movie_id  INTEGER
);

create table if not exists movie
(
    id       INTEGER PRIMARY KEY,
    title    VARCHAR(100),
    released INTEGER,
    tagline  VARCHAR(100)
);

create table if not exists person
(
    id   INTEGER PRIMARY KEY,
    name VARCHAR(100),
    born INTEGER
);

create table if not exists produced
(
    person_id INTEGER,
    movie_id  INTEGER
);