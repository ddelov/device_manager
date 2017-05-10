--DROP SCHEMA openshift cascade;
CREATE SCHEMA IF NOT EXISTS openshift;

--DROP TABLE openshift.dev_ownership;
CREATE TABLE IF NOT EXISTS openshift.dev_ownership(
    id SERIAL NOT NULL PRIMARY KEY, customer_id varchar(100) NOT NULL, thing_name varchar(100) NOT NULL,
     thing_type varchar(100) NOT NULL,
    sn varchar(100) NOT NULL, own boolean not null, valid_from char(8) not null, valid_to char(8),
    status varchar(100) NOT NULL
    );

select * from pg_tables where schemaname='openshift';