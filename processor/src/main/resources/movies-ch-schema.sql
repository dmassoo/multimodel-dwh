create table if not exists movie
(
    id       INTEGER,
    title    VARCHAR(100),
    released INTEGER,
    tagline  VARCHAR(100)
) ENGINE = MergeTree()
ORDER BY released;

create table if not exists person
(
    id   INTEGER,
    name VARCHAR(100),
    born INTEGER
) ENGINE = MergeTree()
ORDER BY id;