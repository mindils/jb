package ru.mindils.jb.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.EmployerInfo;
import ru.mindils.jb.service.entity.EmployerStatusEnum;
import ru.mindils.jb.service.util.HibernateTestUtil;

public class EmployerInfoRepositoryIT {

    private static SessionFactory sessionFactory;
    private static Session session;

    private EmployerInfoRepository employerInfoRepository;
    private EmployerRepository employerRepository;

    @BeforeAll
    static void setUpAll() {
        sessionFactory = HibernateTestUtil.buildSessionFactory();
    }

    @AfterAll
    static void tearDownAll() {
        sessionFactory.close();
    }

    @BeforeEach
    void setUp() {
        session = sessionFactory.openSession();
        session.beginTransaction();
        employerInfoRepository = new EmployerInfoRepository(session);
        employerRepository = new EmployerRepository(session);
    }

    @AfterEach
    void tearDown() {
        session.getTransaction().rollback();
        session.close();
    }

    @Test
    public void save() {
        Employer employer = getEmployer();
        EmployerInfo employerInfo = getEmployerInfo(employer);
        employerRepository.save(employer);
        employerInfoRepository.save(employerInfo);
        session.flush();

        assertThat(employerInfo.getId()).isNotNull();
    }

    @Test
    public void findById() {
        Employer employer = getEmployer();
        EmployerInfo employerInfo = getEmployerInfo(employer);
        employerRepository.save(employer);
        employerInfoRepository.save(employerInfo);
        session.flush();
        session.clear();

        Optional<EmployerInfo> actualResult = employerInfoRepository.findById(employerInfo.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(employerInfo);
    }

    @Test
    public void update() {
        Employer employer = getEmployer();
        EmployerInfo employerInfo = getEmployerInfo(employer);
        employerRepository.save(employer);
        employerInfoRepository.save(employerInfo);
        session.flush();

        employerInfo.setStatus(EmployerStatusEnum.APPROVED);
        employerInfoRepository.update(employerInfo);
        session.flush();
        session.clear();

        Optional<EmployerInfo> actualResult = employerInfoRepository.findById(employerInfo.getId());
        actualResult.ifPresent(e -> assertThat(e).isEqualTo(employerInfo));
    }

    @Test
    public void delete() {
        Employer employer = getEmployer();
        EmployerInfo employerInfo = getEmployerInfo(employer);
        employerRepository.save(employer);
        employerInfoRepository.save(employerInfo);
        session.flush();

        employerInfoRepository.delete(employerInfo);
        session.flush();
        session.clear();

        Optional<EmployerInfo> actualResult = employerInfoRepository.findById(employerInfo.getId());
        assertThat(actualResult.isPresent()).isFalse();
    }

    private static Employer getEmployer() {
        return Employer.builder()
                .id("employer-id-example")
                .name("ООО Рога и копыта")
                .trusted(true)
                .description("Описание работодателя")
                .detailed(true)
                .modifiedAt(Instant.now())
                .createdAt(Instant.now())
                .build();
    }

    private static EmployerInfo getEmployerInfo(Employer employer) {
        return EmployerInfo.builder().employer(employer).status(EmployerStatusEnum.NEW).build();
    }
}
