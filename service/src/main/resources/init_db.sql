CREATE TABLE users
(
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(64)  NOT NULL UNIQUE,
    role        VARCHAR(32)  NOT NULL DEFAULT 'USER',
    password    VARCHAR(128) NOT NULL,
    created_at  TIMESTAMP,
    modified_at TIMESTAMP
);

CREATE TABLE employer
(
    id          VARCHAR(255) NOT NULL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    trusted     BOOLEAN      NOT NULL DEFAULT false,
    description TEXT,
    detailed    BOOLEAN      NOT NULL DEFAULT false,

    created_at  TIMESTAMP,
    modified_at TIMESTAMP
);

CREATE TABLE employer_info
(
    id          BIGSERIAL PRIMARY KEY,
    employer_id VARCHAR(255) NOT NULL UNIQUE REFERENCES employer (id) ON DELETE CASCADE,
    status      VARCHAR(255)
);

CREATE TABLE vacancy
(
    id                   VARCHAR(255) NOT NULL PRIMARY KEY,
    name                 VARCHAR(255) NOT NULL,
    employer_id          VARCHAR(255) NOT NULL REFERENCES employer (id),
    premium              BOOLEAN      NOT NULL DEFAULT false,
    city                 VARCHAR(255),
    salary_gross         BOOLEAN,
    salary_from          INTEGER,
    salary_to            INTEGER,
    salary_currency      VARCHAR(3),
    type                 VARCHAR(255),
    published_at         TIMESTAMP,
    created_at           TIMESTAMP,
    archived             BOOLEAN      NOT NULL DEFAULT false,
    apply_alternate_url  VARCHAR(255),
    url                  VARCHAR(255),
    alternate_url        VARCHAR(255),
    schedule             VARCHAR(255),
    response_url         VARCHAR(255),
    professional_roles   JSONB,
    employment           VARCHAR(255),
    description          TEXT,
    key_skills           TEXT,
    detailed             BOOLEAN      NOT NULL DEFAULT false,

    internal_created_at  TIMESTAMP,
    internal_modified_at TIMESTAMP
);

CREATE TABLE vacancy_info
(
    id          BIGSERIAL PRIMARY KEY,
    vacancy_id  VARCHAR(255) NOT NULL UNIQUE REFERENCES vacancy (id) ON DELETE CASCADE,
    ai_approved NUMERIC,
    approved    BOOLEAN,
    status      VARCHAR(50)
);


CREATE TABLE vacancy_filter
(
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP,
    modified_at TIMESTAMP
);

CREATE TABLE vacancy_filter_params
(
    id                BIGSERIAL PRIMARY KEY,
    vacancy_filter_id BIGINT       NOT NULL REFERENCES vacancy_filter (id) ON DELETE CASCADE,
    param_name        VARCHAR(255) NOT NULL,
    param_value       VARCHAR(255) NOT NULL
);

INSERT INTO vacancy_filter (code, name, created_at, modified_at)
VALUES ('DEFAULT', 'Фильтр по умолчанию', now(), now());

INSERT INTO vacancy_filter_params (vacancy_filter_id, param_name, param_value)
VALUES ((select id from vacancy_filter where code = 'DEFAULT'), 'area', '1'),
       ((select id from vacancy_filter where code = 'DEFAULT'), 'professional_role', '96'),
       ((select id from vacancy_filter where code = 'DEFAULT'), 'professional_role', '104'),
       ((select id from vacancy_filter where code = 'DEFAULT'), 'salary', '200000'),
       ((select id from vacancy_filter where code = 'DEFAULT'), 'currency_code', 'RUR');

/*
 *  Для возможности редактировать vacancy_info во view
 */
CREATE VIEW vacancy_view AS
SELECT v.id,
       v.name,
       v.employer_id,
       v.premium,
       v.city,
       v.salary_gross,
       v.salary_from,
       v.salary_to,
       v.salary_currency,
       v.type,
       v.published_at,
       v.created_at,
       v.archived,
       v.apply_alternate_url,
       v.url,
       v.alternate_url,
       v.schedule,
       v.response_url,
       v.professional_roles,
       v.employment,
       v.description,
       v.key_skills,
       v.detailed,
       v.internal_created_at,
       v.internal_modified_at,
       i.ai_approved,
       i.approved,
       i.status
FROM vacancy v
         LEFT JOIN vacancy_info i ON v.id = i.vacancy_id;

CREATE OR REPLACE FUNCTION update_or_insert_vacancy_info_status()
    RETURNS trigger AS
$$
BEGIN
    INSERT INTO vacancy_info(vacancy_id, status, approved)
    VALUES (NEW.id, NEW.status, NEW.approved)
    ON CONFLICT (vacancy_id) DO UPDATE
        SET status = EXCLUDED.status, approved = EXCLUDED.approved;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_or_insert_vacancy_view_status
    INSTEAD OF UPDATE
    ON vacancy_view
    FOR EACH ROW
EXECUTE FUNCTION update_or_insert_vacancy_info_status();