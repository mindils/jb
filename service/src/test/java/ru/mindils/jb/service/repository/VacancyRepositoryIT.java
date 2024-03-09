package ru.mindils.jb.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Salary;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyStatusEnum;
import ru.mindils.jb.service.util.HibernateTestUtil;
import ru.mindils.jb.service.util.TestDataImporter;

public class VacancyRepositoryIT {

    private static SessionFactory sessionFactory;
    private static Session session;

    private VacancyRepository vacancyRepository;
    private EmployerRepository employerRepository;

    @BeforeAll
    static void setUpAll() {
        sessionFactory = HibernateTestUtil.buildSessionFactory();
        TestDataImporter.importData(sessionFactory);
    }

    @AfterAll
    static void tearDownAll() {
        sessionFactory.close();
    }

    @BeforeEach
    void setUp() {
        session = sessionFactory.openSession();
        session.beginTransaction();
        vacancyRepository = new VacancyRepository(session);
        employerRepository = new EmployerRepository(session);
    }

    @AfterEach
    void tearDown() {
        session.getTransaction().rollback();
        session.close();
    }

    @Test
    public void findByFilter() {
        AppVacancyFilterDto filter =
                AppVacancyFilterDto.builder()
                        .aiApproved(BigDecimal.valueOf(0.7))
                        .status(VacancyStatusEnum.APPROVED)
                        .salaryFrom(100000)
                        .salaryTo(300000)
                        .build();

        List<Vacancy> actualResult = vacancyRepository.findByFilter(filter);

        assertThat(actualResult).hasSize(1);
    }

    @Test
    public void save() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);

        employerRepository.save(employer);
        vacancyRepository.save(vacancy);

        assertThat(vacancy.getId()).isNotNull();
    }

    @Test
    public void findById() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);

        employerRepository.save(employer);
        vacancyRepository.save(vacancy);
        session.flush();
        session.clear();

        Optional<Vacancy> actualResult = vacancyRepository.findById(vacancy.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(vacancy);
    }

    @Test
    public void update() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);

        employerRepository.save(employer);
        vacancyRepository.save(vacancy);
        session.flush();

        vacancy.setName("new Vacancy");
        vacancyRepository.update(vacancy);
        session.flush();
        session.clear();

        Optional<Vacancy> actualResult = vacancyRepository.findById(vacancy.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(vacancy);
    }

    @Test
    public void delete() {
        Employer employer = getEmployer();
        Vacancy vacancy = getVacancy(employer);

        employerRepository.save(employer);
        vacancyRepository.save(vacancy);
        session.flush();

        vacancyRepository.delete(vacancy);
        session.flush();
        session.clear();

        Optional<Vacancy> actualResult = vacancyRepository.findById(vacancy.getId());
        assertThat(actualResult.isPresent()).isFalse();
    }

    private static Employer getEmployer() {
        return Employer.builder()
                .id("employer-id-example")
                .name("ООО Рога и копыта")
                .trusted(true)
                .description("Описание работодателя")
                .detailed(true)
                .createdAt(Instant.now())
                .build();
    }

    private static Vacancy getVacancy(Employer employer) {
        return Vacancy.builder()
                .id("unique-vacancy-id")
                .name("Разработчик Java")
                .employer(employer)
                .premium(false)
                .city("Москва")
                .salary(
                        Salary.builder()
                                .from(100000)
                                .to(150000)
                                .currency("RUR")
                                .gross(true)
                                .build())
                .type("open")
                .publishedAt(Instant.now())
                .createdAt(Instant.now())
                .archived(false)
                .applyAlternateUrl("https://example.com")
                .url("https://example.com")
                .alternateUrl("https://example.com")
                .schedule("fullDay")
                .responseUrl("https://example.com")
                .professionalRoles(List.of(Map.of("id", "1", "name", "Java Developer")))
                .employment("full")
                .description("Описание вакансии")
                .build();
    }
}
