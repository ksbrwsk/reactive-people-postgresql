CREATE TABLE person
(
    id      BIGSERIAL,
    name    varchar(255) NULL,
    vorname varchar(255) NULL,
    CONSTRAINT partner_pkey PRIMARY KEY (id)
);
create sequence partner_seq increment 1 start 1 minvalue 1;
