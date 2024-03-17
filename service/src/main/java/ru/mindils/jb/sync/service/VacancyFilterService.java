package ru.mindils.jb.sync.service;

import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mindils.jb.service.entity.VacancyFilter;

@Service
@RequiredArgsConstructor
public class VacancyFilterService {

    private static final String VACANCY_FILTER_DEFAULT = "DEFAULT";
    private final EntityManager entityManager;

    public List<Map<String, String>> getDefaultFilter() {
        List<VacancyFilter> result =
                entityManager
                        .createQuery("from VacancyFilter where code = :filter", VacancyFilter.class)
                        .setParameter("filter", VACANCY_FILTER_DEFAULT)
                        .getResultList();

        VacancyFilter vacancyFilter = result.isEmpty() ? null : result.get(0);

        if (vacancyFilter == null) {
            return Collections.emptyList();
        } else {
            return vacancyFilter.getParams().stream()
                    .map(param -> Map.of(param.getParamName(), param.getParamValue()))
                    .toList();
        }
    }
}
