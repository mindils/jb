/*
 *  Для возможности редактировать vacancy_info во view
 *  TODO:
 *   почему-то не работает создание FUNCTION в liquibase (подумать как решить)
 */
CREATE VIEW jb_v_vacancy AS
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
FROM jb_vacancy v
         LEFT JOIN jb_vacancy_info i ON v.id = i.vacancy_id;

CREATE OR REPLACE FUNCTION update_or_insert_vacancy_info_status()
    RETURNS trigger AS
$$
BEGIN
    INSERT INTO jb_vacancy_info(vacancy_id, status, approved)
    VALUES (NEW.id, NEW.status, NEW.approved)
    ON CONFLICT (vacancy_id) DO UPDATE
        SET status = EXCLUDED.status, approved = EXCLUDED.approved;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_or_insert_vacancy_view_status
    INSTEAD OF UPDATE
    ON jb_v_vacancy
    FOR EACH ROW
EXECUTE FUNCTION update_or_insert_vacancy_info_status();