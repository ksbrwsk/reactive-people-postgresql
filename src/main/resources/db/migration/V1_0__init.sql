CREATE TABLE public.person
(
    id      BIGSERIAL,
    name    varchar(255) NULL,
    vorname varchar(255) NULL,
    CONSTRAINT person_pkey PRIMARY KEY (id)
);
create sequence public.person_seq increment 1 start 1 minvalue 1;
