--liquibase formatted sql

--changeset mindils:1
ALTER TABLE jb_vacancy_job_execution
    ADD COLUMN progress JSONB
