package ru.mindils.jb.integration.sync.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.integration.service.ITBase;
import ru.mindils.jb.sync.service.VacancyFilterService;

@RequiredArgsConstructor
class VacancyFilterServiceTest extends ITBase {

    private final VacancyFilterService vacancyFilterService;

    @Test
    void getDefaultFilter() {
        List<Map<String, String>> actualResult = vacancyFilterService.getDefaultFilter();

        assertThat(actualResult).isNotEmpty();
    }
}
