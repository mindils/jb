package ru.mindils.jb.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.service.entity.Employer;

public class EmployerRepositoryIT extends BaseRepositoryIT {
    private static EmployerRepository employerRepository;

    @BeforeAll
    static void setUpAll() {
        init();
        employerRepository = context.getBean(EmployerRepository.class);
    }

    @Test
    void save() {
        Employer employer = getEmployer();
        employerRepository.save(employer);
        entityManager.flush();

        assertThat(employer.getId()).isNotNull();
    }

    @Test
    void findById() {
        Employer employer = getEmployer();
        employerRepository.save(employer);
        entityManager.flush();
        entityManager.clear();

        Optional<Employer> actualResult = employerRepository.findById(employer.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(employer);
    }

    @Test
    void update() {
        Employer employer = getEmployer();
        employerRepository.save(employer);
        entityManager.flush();

        employer.setName("ООО Рога и копыта 2");
        employerRepository.update(employer);
        entityManager.flush();
        entityManager.clear();

        Optional<Employer> actualResult = employerRepository.findById(employer.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(employer);
    }

    @Test
    void delete() {
        Employer employer = getEmployer();
        employerRepository.save(employer);
        entityManager.flush();

        employerRepository.delete(employer);
        entityManager.flush();
        entityManager.clear();

        Optional<Employer> actualResult = employerRepository.findById(employer.getId());

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
                .createdAt(Instant.now())
                .build();
    }
}
