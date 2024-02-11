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
    id                VARCHAR(255) NOT NULL PRIMARY KEY,
    name              VARCHAR(255) NOT NULL,
    trusted           BOOLEAN      NOT NULL DEFAULT false,
    description       TEXT,

    is_detailed       BOOLEAN      NOT NULL DEFAULT false,

    local_created_at  TIMESTAMP,
    local_modified_at TIMESTAMP
);

CREATE TABLE vacancy
(
    id                  VARCHAR(255) NOT NULL PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    employer_id         VARCHAR(255),
    premium             BOOLEAN      NOT NULL DEFAULT false,
    city                VARCHAR(255),
    salary_gross        BOOLEAN,
    salary_from         INTEGER,
    salary_to           INTEGER,
    salary_currency     VARCHAR(3),
    type                VARCHAR(255),
    published_at        TIMESTAMP,
    created_at          TIMESTAMP,
    archived            BOOLEAN      NOT NULL DEFAULT false,
    apply_alternate_url VARCHAR(255),
    url                 VARCHAR(255),
    alternate_url       VARCHAR(255),
    schedule            VARCHAR(255),
    response_url        VARCHAR(255),

    professional_roles  JSONB,
    employment          VARCHAR(255),

    description         TEXT,
    key_skills          VARCHAR(255),

    -- флаг, была ли загружена детальная информация о вакансии
    is_detailed         BOOLEAN      NOT NULL DEFAULT false,

    -- дата создания и модификации записи сервисом
    local_created_at    TIMESTAMP,

    -- дата последней модификации записи сервисом
    local_modified_at   TIMESTAMP
);

CREATE TABLE vacancy_info
(
    id          BIGSERIAL PRIMARY KEY,
    vacancy_id  VARCHAR(255) NOT NULL,
    ai_approved NUMERIC,

    -- NEW, IN_PROGRESS, APPROVED, DECLINED
    status      VARCHAR(50)
);

CREATE TABLE employer_info
(
    id          BIGSERIAL PRIMARY KEY,
    employer_id VARCHAR(255) NOT NULL,
    -- NEW, IN_PROGRESS, APPROVED, DECLINED
    status      VARCHAR(255)
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
    vacancy_filter_id BIGINT       NOT NULL,
    param_name        VARCHAR(255) NOT NULL,
    param_value       VARCHAR(255) NOT NULL
);