package ru.mindils.jb.service.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mindils.jb.service.dto.AppVacancyFilterDto;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyStatusEnum;

public class VacancyCriteriaApiFilterBuilderTest {

  @Mock
  private CriteriaBuilder criteriaBuilder;

  @Mock
  private Root<Vacancy> root;

  @Mock
  private Predicate mockPredicate;

  @Mock
  private Path<Object> vacancyInfoPath;

  @Mock
  private Path<Object> aiApprovedPath;

  @Mock
  private Path<Object> salaryFromPath;

  @Mock
  private Path<Object> salaryToPath;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    when(root.get("vacancyInfo")).thenReturn(vacancyInfoPath);
    when(vacancyInfoPath.get("aiApproved")).thenReturn(aiApprovedPath);
    when(root.get("salary")).thenReturn(vacancyInfoPath);
    when(vacancyInfoPath.get("from")).thenReturn(salaryFromPath);
    when(vacancyInfoPath.get("to")).thenReturn(salaryToPath);

    Path<Object> statusPath = mock(Path.class);
    when(vacancyInfoPath.get("status")).thenReturn(statusPath);

    when(criteriaBuilder.greaterThan(any(Expression.class), any(BigDecimal.class)))
        .thenReturn(mockPredicate);
    when(criteriaBuilder.lessThan(any(Expression.class), any(Integer.class)))
        .thenReturn(mockPredicate);
    when(criteriaBuilder.equal(statusPath, VacancyStatusEnum.NEW)).thenReturn(mockPredicate);
    when(criteriaBuilder.or(any(Predicate.class), any(Predicate.class))).thenReturn(mockPredicate);
  }

  @Test
  void buildFilters_withAiApprovalCondition() {
    AppVacancyFilterDto filter = AppVacancyFilterDto.builder().build();
    Predicate[] predicates = VacancyCriteriaApiFilterBuilder.build(filter, criteriaBuilder, root);

    verify(criteriaBuilder).greaterThan(any(), any(BigDecimal.class));
    assertThat(predicates).hasSize(1);
  }

  @Test
  void buildFilters_withSalaryFromCondition() {
    AppVacancyFilterDto filter = AppVacancyFilterDto.builder().salaryFrom(30000).build();
    Predicate[] predicates = VacancyCriteriaApiFilterBuilder.build(filter, criteriaBuilder, root);

    verify(criteriaBuilder).or(any(), any());
    assertThat(predicates).hasSize(2);
  }

  @Test
  void buildFilters_withSalaryToCondition() {
    AppVacancyFilterDto filter = AppVacancyFilterDto.builder().salaryTo(50000).build();
    Predicate[] predicates = VacancyCriteriaApiFilterBuilder.build(filter, criteriaBuilder, root);

    verify(criteriaBuilder).or(any(), any());
    assertThat(predicates).hasSize(2);
  }

  @Test
  void buildFilters_withStatusCondition() {
    AppVacancyFilterDto filter =
        AppVacancyFilterDto.builder().status(VacancyStatusEnum.NEW).build();
    Predicate[] predicates = VacancyCriteriaApiFilterBuilder.build(filter, criteriaBuilder, root);

    verify(criteriaBuilder).equal(any(), eq(VacancyStatusEnum.NEW));
    assertThat(predicates).hasSize(2);
  }

  @Test
  void buildFilters_withCombinedConditions() {
    AppVacancyFilterDto filter = AppVacancyFilterDto.builder()
        .salaryFrom(30000)
        .salaryTo(50000)
        .status(VacancyStatusEnum.NEW)
        .build();
    Predicate[] predicates = VacancyCriteriaApiFilterBuilder.build(filter, criteriaBuilder, root);
    verify(criteriaBuilder, times(2)).or(any(), any());
    verify(criteriaBuilder).equal(any(), eq(VacancyStatusEnum.NEW));
    assertThat(predicates).hasSize(4);
  }

  @Test
  void buildFilters_withEmptyFilter() {
    AppVacancyFilterDto filter = AppVacancyFilterDto.builder().build();
    Predicate[] predicates = VacancyCriteriaApiFilterBuilder.build(filter, criteriaBuilder, root);
    verify(criteriaBuilder).greaterThan(any(), any(BigDecimal.class));
    assertThat(predicates).hasSize(1);
  }

  @Test
  void buildFilters_withNullStatus() {
    AppVacancyFilterDto filter = AppVacancyFilterDto.builder().status(null).build();
    Predicate[] predicates = VacancyCriteriaApiFilterBuilder.build(filter, criteriaBuilder, root);
    verify(criteriaBuilder, never()).equal(any(), any());
    assertThat(predicates).hasSize(1);
  }

  @Test
  void buildFilters_withNullSalary() {
    AppVacancyFilterDto filter =
        AppVacancyFilterDto.builder().salaryFrom(null).salaryTo(null).build();
    Predicate[] predicates = VacancyCriteriaApiFilterBuilder.build(filter, criteriaBuilder, root);
    verify(criteriaBuilder, never()).or(any(), any());
    assertThat(predicates).hasSize(1);
  }
}
