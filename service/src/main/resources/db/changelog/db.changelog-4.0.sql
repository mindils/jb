--liquibase formatted sql

--changeset mindils:1
ALTER TABLE jb_employer
    ADD COLUMN accredited_it_employer BOOLEAN,
    ADD COLUMN type                   VARCHAR(255),
    ADD COLUMN site_url               VARCHAR(255),
    ADD COLUMN alternate_url          VARCHAR(255),
    ADD COLUMN logo_urls_original     VARCHAR(255),
    ADD COLUMN area_name              VARCHAR(255),
    ADD COLUMN industries             JSONB
