package ru.mindils.jb.sync.service;

import static java.nio.charset.StandardCharsets.*;
import static java.util.stream.Collectors.*;

import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import ru.mindils.jb.service.util.HttpClientProvider;
import ru.mindils.jb.sync.dto.DetailedEmployerDto;
import ru.mindils.jb.sync.dto.DetailedVacancyDto;
import ru.mindils.jb.sync.dto.VacancyListResponseDto;

public class VacancyClientService {

    private static final VacancyClientService INSTANCE = new VacancyClientService();

    private static final String VACANCY_API_URL = "https://api.hh.ru/vacancies";
    private static final String EMPLOYER_API_URL = "https://api.hh.ru/employers";
    private static final String AI_RATING_API_URL = "http://89.223.124.23:8080";

    private VacancyClientService() {}

    public static VacancyClientService getInstance() {
        return INSTANCE;
    }

    @SneakyThrows
    public String loadAIRatingByText(String queryParam) {
        String encodedQueryParam = URLEncoder.encode(queryParam, UTF_8);
        String fullUrl = String.format("%s?text=%s", AI_RATING_API_URL, encodedQueryParam);

        return HttpClientProvider.getInstance().retrieve(URI.create(fullUrl), String.class);
    }

    @SneakyThrows
    public DetailedVacancyDto loadVacancyById(String id) {
        return HttpClientProvider.getInstance()
                .retrieve(URI.create(VACANCY_API_URL + "/" + id), DetailedVacancyDto.class);
    }

    @SneakyThrows
    public DetailedEmployerDto loadEmployerById(String id) {
        return HttpClientProvider.getInstance()
                .retrieve(URI.create(EMPLOYER_API_URL + "/" + id), DetailedEmployerDto.class);
    }

    @SneakyThrows
    public VacancyListResponseDto loadVacancies(List<Map<String, String>> params) {
        return HttpClientProvider.getInstance()
                .retrieve(
                        buildURIWithParams(VACANCY_API_URL, params), VacancyListResponseDto.class);
    }

    private URI buildURIWithParams(String uri, List<Map<String, String>> params) {
        return URI.create(
                uri
                        + params.stream()
                                .flatMap(map -> map.entrySet().stream())
                                .map(entry -> entry.getKey() + "=" + entry.getValue())
                                .collect(joining("&", "?", "")));
    }
}
