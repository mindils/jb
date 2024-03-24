--liquibase formatted sql

--changeset mindils:1
CREATE TABLE jb_vacancy_job_execution
(
    id            BIGSERIAL PRIMARY KEY,
    priority      INTEGER,
    step          SMALLINT  NOT NULL, -- "load_vacancies", "load_details", "load_companies", "load_ratings".
    status        VARCHAR(50),        -- "RUNNING", "COMPLETED", "FAILED".
    parameters    JSONB,
    start_time    TIMESTAMP,
    end_time      TIMESTAMP,
    error_message TEXT,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
