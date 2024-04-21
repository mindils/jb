--liquibase formatted sql

--changeset mindils:1
CREATE TABLE jb_sync_log
(
    id         BIGSERIAL PRIMARY KEY,
    entity_id  VARCHAR(255),
    data_type  VARCHAR(255),
    data       JSONB,
    status     VARCHAR(255),
    created_at TIMESTAMP
);