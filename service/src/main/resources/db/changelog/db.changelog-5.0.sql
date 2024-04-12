--liquibase formatted sql

--changeset mindils:1
ALTER TABLE users
    ADD COLUMN firstname VARCHAR(255),
    ADD COLUMN email     VARCHAR(255),
    ADD COLUMN enabled   BOOLEAN
