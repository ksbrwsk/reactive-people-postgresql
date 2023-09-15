drop table if exists person;
create table person
(
    id      bigserial primary key,
    name    varchar(255),
    vorname varchar(255)
);
