--DROP SCHEMA openshift cascade;
CREATE SCHEMA IF NOT EXISTS openshift;

--DROP TABLE openshift.customer;
CREATE TABLE IF NOT EXISTS openshift.customer(
    id SERIAL NOT NULL PRIMARY KEY, username varchar(100) NOT NULL, password varchar(100) NOT NULL, email varchar(100) NOT NULL unique);

--DROP TABLE openshift.shadow_data;
CREATE TABLE IF NOT EXISTS openshift.shadow_data(
    thing_name varchar(100) NOT NULL PRIMARY KEY, tstamp timestamp NOT NULL, reported varchar(255), desired varchar(255));

--DROP TABLE openshift.dev_ownership;
CREATE TABLE IF NOT EXISTS openshift.dev_ownership(
    id SERIAL NOT NULL PRIMARY KEY, cust_id integer NOT NULL, thing_name varchar(100) NOT NULL, thing_type varchar(100) NOT NULL,
    sn varchar(100) NOT NULL, own boolean not null, valid_from char(8) not null, valid_to char(8),
    FOREIGN KEY (cust_id) REFERENCES openshift.customer(id),
    FOREIGN KEY (thing_name) REFERENCES openshift.shadow_data(thing_name));
commit;

select * from pg_tables where schemaname='openshift';