package ru.mindils.jb.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.service.entity.VacancyFilter;
import ru.mindils.jb.service.entity.VacancyFilterParams;

public class VacancyFilterRepositoryIT extends BaseRepositoryIT {

    private static VacancyFilterRepository vacancyFilterRepository;

    @BeforeAll
    static void setUpAll() {
        init();
        vacancyFilterRepository = context.getBean(VacancyFilterRepository.class);
    }

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

        Optional<VacancyFilter> actualResult =
                vacancyFilterRepository.findById(vacancyFilter.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(vacancyFilter);
    }

    @Test
    void update() {
        VacancyFilter vacancyFilter = getVacancyFilter();
        vacancyFilterRepository.save(vacancyFilter);
        entityManager.flush();

        vacancyFilter.setName("newName");
        vacancyFilterRepository.update(vacancyFilter);
        entityManager.flush();
        entityManager.clear();

        Optional<VacancyFilter> actualResult =
                vacancyFilterRepository.findById(vacancyFilter.getId());

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

        Optional<VacancyFilter> actualResult =
                vacancyFilterRepository.findById(vacancyFilter.getId());
        assertThat(actualResult.isPresent()).isFalse();
    }

    private static VacancyFilter getVacancyFilter() {
        VacancyFilter vacancyFilter =
                VacancyFilter.builder()
                        .code("default")
                        .name("Фильтр по умолчанию")
                        .createdAt(Instant.now())
                        .modifiedAt(Instant.now())
                        .build();

        vacancyFilter.addParam(
                VacancyFilterParams.builder().paramName("city").paramValue("1").build());

        vacancyFilter.addParam(
                VacancyFilterParams.builder().paramName("text").paramValue("java").build());

        return vacancyFilter;
    }
}
