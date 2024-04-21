--liquibase formatted sql

--changeset mindils:1
ALTER TABLE users
    ADD COLUMN image VARCHAR;
