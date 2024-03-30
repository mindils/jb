package ru.mindils.jb.integration.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.integration.service.ITBase;
import ru.mindils.jb.integration.service.annotation.IT;
import ru.mindils.jb.service.entity.VacancyFilter;
import ru.mindils.jb.service.entity.VacancyFilterParams;
import ru.mindils.jb.service.repository.VacancyFilterRepository;

@IT
@RequiredArgsConstructor
public class VacancyFilterRepositoryIT extends ITBase {

    private final VacancyFilterRepository vacancyFilterRepository;
    private final EntityManager entityManager;

    @Test
    void save() {
        VacancyFilter vacancyFilter = getVacancyFilter();
        vacancyFilterRepository.save(vacancyFilter);

        assertThat(vacancyFilter.getId()).isNotNull();
    }

    @Test
    void findById() {
        VacancyFilter vacancyFilter = getVacancyFilter();
        vacancyFilterRepository.save(vacancyFilter);
        entityManager.flush();
        entityManager.clear();

        Optional<VacancyFilter> actualResult = vacancyFilterRepository.findById(vacancyFilter.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(vacancyFilter);
    }

    @Test
    void update() {
        VacancyFilter vacancyFilter = getVacancyFilter();
        vacancyFilterRepository.save(vacancyFilter);
        entityManager.flush();

        vacancyFilter.setName("newName");
        vacancyFilterRepository.save(vacancyFilter);
        entityManager.flush();
        entityManager.clear();

        Optional<VacancyFilter> actualResult = vacancyFilterRepository.findById(vacancyFilter.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(vacancyFilter);
    }

    @Test
    void delete() {
        VacancyFilter vacancyFilter = getVacancyFilter();
        vacancyFilterRepository.save(vacancyFilter);
        entityManager.flush();

        vacancyFilterRepository.delete(vacancyFilter);
        entityManager.flush();
        entityManager.clear();

        Optional<VacancyFilter> actualResult = vacancyFilterRepository.findById(vacancyFilter.getId());
        assertThat(actualResult.isPresent()).isFalse();
    }

    private static VacancyFilter getVacancyFilter() {
        VacancyFilter vacancyFilter = VacancyFilter.builder()
                .code("default")
                .name("Фильтр по умолчанию")
                .createdAt(Instant.now())
                .modifiedAt(Instant.now())
                .build();

        vacancyFilter.addParam(
                VacancyFilterParams.builder().paramName("city").paramValue("1").build());

        vacancyFilter.addParam(VacancyFilterParams.builder()
                .paramName("text")
                .paramValue("java")
                .build());

        return vacancyFilter;
    }
}
