--liquibase formatted sql

--changeset mindils:1
ALTER TABLE jb_vacancy_info
    ADD COLUMN version BIGINT
