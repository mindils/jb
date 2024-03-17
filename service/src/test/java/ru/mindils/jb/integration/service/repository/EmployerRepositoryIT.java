package ru.mindils.jb.integration.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.mindils.jb.integration.service.ITBase;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.repository.EmployerRepository;

@RequiredArgsConstructor
@Transactional
public class EmployerRepositoryIT extends ITBase {

    private final EmployerRepository employerRepository;
    private final EntityManager entityManager;

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
