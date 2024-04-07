-- Вставка данных для работодателей
INSERT INTO jb_employer (id, name, trusted, description, detailed, created_at)
VALUES
    ('employer-id-1', 'ООО Рога и Копыта', true, 'Описание работодателя ООО Рога и Копыта', true, now()),
    ('employer-id-2', 'ЗАО Инновации', false, 'Описание работодателя ЗАО Инновации', true, now()),
    ('employer-id-3', 'ОАО СтройМонтаж', true, 'Описание работодателя ОАО СтройМонтаж', true, now()),
    ('employer-id-4', 'ИП Петров', false, 'Описание работодателя ИП Петров', true, now()),
    ('employer-id-5', 'ТОО Разработка', true, 'Описание работодателя ТОО Разработка', true, now());

-- Вставка данных для вакансий
-- 3 новые вакансии с aiApproved > 0.7
INSERT INTO jb_vacancy (id, name, employer_id, premium, city, salary_gross, salary_from, salary_to, salary_currency, type, published_at, created_at, archived, apply_alternate_url, url, alternate_url, schedule, response_url, professional_roles, employment, description)
VALUES
    ('vacancy-id-1', 'Разработчик Java #1', 'employer-id-1', false, 'Москва', true, 120000, 180000, 'RUR', 'open', now(), now(), false, 'https://hh.ru', 'https://hh.ru', 'https://hh.ru', 'fullDay', 'https://hh.ru', '[{"id": "1", "name": "Java Developer"}]', 'full', 'Описание вакансии Разработчик Java #1'),
    ('vacancy-id-2', 'Разработчик Python #1', 'employer-id-2', false, 'Санкт-Петербург', true, 130000, 190000, 'RUR', 'open', now(), now(), false, 'https://hh.ru', 'https://hh.ru', 'https://hh.ru', 'fullDay', 'https://hh.ru', '[{"id": "2", "name": "Python Developer"}]', 'full', 'Описание вакансии Разработчик Python #1'),
    ('vacancy-id-3', 'Data Scientist #1', 'employer-id-3', true, 'Новосибирск', true, 140000, 200000, 'RUR', 'open', now(), now(), false, 'https://hh.ru', 'https://hh.ru', 'https://hh.ru', 'fullDay', 'https://hh.ru', '[{"id": "3", "name": "Data Scientist"}]', 'full', 'Описание вакансии Data Scientist #1');

-- 1 одобренная вакансия с aiApproved > 0.7
INSERT INTO jb_vacancy (id, name, employer_id, premium, city, salary_gross, salary_from, salary_to, salary_currency, type, published_at, created_at, archived, apply_alternate_url, url, alternate_url, schedule, response_url, professional_roles, employment, description)
VALUES
    ('vacancy-id-4', 'DevOps Engineer #1', 'employer-id-4', true, 'Екатеринбург', true, 150000, 210000, 'RUR', 'open', now(), now(), false, 'https://hh.ru', 'https://hh.ru', 'https://hh.ru', 'fullDay', 'https://hh.ru', '[{"id": "4", "name": "DevOps Engineer"}]', 'full', 'Описание вакансии DevOps Engineer #1');

-- 1 отклоненная вакансия с aiApproved > 0.7
INSERT INTO jb_vacancy (id, name, employer_id, premium, city, salary_gross, salary_from, salary_to, salary_currency, type, published_at, created_at, archived, apply_alternate_url, url, alternate_url, schedule, response_url, professional_roles, employment, description)
VALUES
    ('vacancy-id-5', 'UI/UX Designer #1', 'employer-id-5', false, 'Казань', true, 110000, 170000, 'RUR', 'open', now(), now(), true, 'https://hh.ru', 'https://hh.ru', 'https://hh.ru', 'partTime', 'https://hh.ru', '[{"id": "5", "name": "UI/UX Designer"}]', 'part', 'Описание вакансии UI/UX Designer #1');

-- 1 новая вакансия с aiApproved < 0.7
INSERT INTO jb_vacancy (id, name, employer_id, premium, city, salary_gross, salary_from, salary_to, salary_currency, type, published_at, created_at, archived, apply_alternate_url, url, alternate_url, schedule, response_url, professional_roles, employment, description)
VALUES
    ('vacancy-id-6', 'Project Manager #1', 'employer-id-1', true, 'Москва', true, 160000, 220000, 'RUR', 'open', now(), now(), false, 'https://hh.ru', 'https://hh.ru', 'https://hh.ru', 'fullDay', 'https://hh.ru', '[{"id": "1", "name": "Project Manager"}]', 'full', 'Описание вакансии Project Manager #1');

-- 1 новая вакансия без зарплаты с aiApproved < 0.7
INSERT INTO jb_vacancy (id, name, employer_id, premium, city, salary_gross, salary_from, salary_to, salary_currency, type, published_at, created_at, archived, apply_alternate_url, url, alternate_url, schedule, response_url, professional_roles, employment, description)
VALUES
    ('vacancy-id-7', 'Business Analyst #1', 'employer-id-2', false, 'Санкт-Петербург', false, NULL, NULL, NULL, 'open', now(), now(), false, 'https://hh.ru', 'https://hh.ru', 'https://hh.ru', 'remote', 'https://hh.ru', '[{"id": "2", "name": "Business Analyst"}]', 'full', 'Описание вакансии Business Analyst #1');

-- 1 отклоненная вакансия без зарплаты с aiApproved > 0.7
INSERT INTO jb_vacancy (id, name, employer_id, premium, city, salary_gross, salary_from, salary_to, salary_currency, type, published_at, created_at, archived, apply_alternate_url, url, alternate_url, schedule, response_url, professional_roles, employment, description)
VALUES
    ('vacancy-id-8', 'System Administrator #1', 'employer-id-3', true, 'Новосибирск', false, NULL, NULL, NULL, 'open', now(), now(), true, 'https://hh.ru', 'https://hh.ru', 'https://hh.ru', 'shift', 'https://hh.ru', '[{"id": "3", "name": "System Administrator"}]', 'full', 'Описание вакансии System Administrator #1');

-- Вставка данных для информации о вакансиях
INSERT INTO jb_vacancy_info (vacancy_id, ai_approved, approved, status)
VALUES
    ('vacancy-id-1', 0.71, false, 'NEW'),
    ('vacancy-id-2', 0.72, false, 'NEW'),
    ('vacancy-id-3', 0.73, false, 'NEW'),
    ('vacancy-id-4', 0.74, true, 'APPROVED'),
    ('vacancy-id-5', 0.75, false, 'DECLINED'),
    ('vacancy-id-6', 0.65, false, 'NEW'),
    ('vacancy-id-7', 0.60, false, 'NEW'),
    ('vacancy-id-8', 0.76, false, 'DECLINED');


-- Вставка данных user
INSERT INTO users (id, username, role, password)
VALUES
    (1, 'admin', 'ADMIN', '{noop}1'),
    (2, 'user1', 'USER', '{noop}2'),
    (3, 'user2', 'USER', '{noop}3');
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users) + 1);


