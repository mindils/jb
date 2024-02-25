package ru.mindils.jb.service.service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.mindils.jb.service.dto.DetailedEmployerDto;
import ru.mindils.jb.service.dto.VacancyListResponseDto;
import ru.mindils.jb.service.dto.DetailedVacancyDto;
import ru.mindils.jb.service.util.HttpClientProvider;

public class VacancyClientService {

  @Getter
  private static final VacancyClientService instance = new VacancyClientService();

  private final String VACANCY_API_URL = "https://api.hh.ru/vacancies";
  private final String EMPLOYER_API_URL = "https://api.hh.ru/employers";
  private final String AI_RATING_API_URL = "http://89.223.124.23:8080";

  private VacancyClientService() {
  }

  @SneakyThrows
  public String loadAIRatingByText(String queryParam) {
    String encodedQueryParam = URLEncoder.encode(queryParam, StandardCharsets.UTF_8);
    String fullUrl = AI_RATING_API_URL + "?text=" + encodedQueryParam;

    return HttpClientProvider
        .get(URI.create(fullUrl))
        .retrieve(String.class);
  }

  @SneakyThrows
  public DetailedVacancyDto loadVacancyById(String id) {
    return HttpClientProvider
        .get(URI.create(VACANCY_API_URL + "/" + id))
        .retrieve(DetailedVacancyDto.class);
  }

  @SneakyThrows
  public DetailedEmployerDto loadEmployerById(String id) {
    return HttpClientProvider
        .get(URI.create(EMPLOYER_API_URL + "/" + id))
        .retrieve(DetailedEmployerDto.class);
  }

  @SneakyThrows
  public VacancyListResponseDto loadVacancies(List<Map<String, String>> params) {
    return HttpClientProvider
        .get(buildURIWithParams(VACANCY_API_URL, params))
        .retrieve(VacancyListResponseDto.class);
  }

  private URI buildURIWithParams(String uri, List<Map<String, String>> params) {
    return URI.create(uri + params.stream()
        .flatMap(map -> map.entrySet().stream())
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .collect(Collectors.joining("&", "?", "")));
  }
}
