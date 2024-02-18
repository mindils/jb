package ru.mindils.jb.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "vacancy_filter")
public class VacancyFilter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String code;
  private String name;

  @Builder.Default
  @OneToMany(mappedBy = "vacancyFilter", fetch = FetchType.LAZY)
  private List<VacancyFilterParams> params = new ArrayList<>();

  private Instant createdAt;
  private Instant modifiedAt;

  public void addParam(VacancyFilterParams param) {
    params.add(param);
    param.setVacancyFilter(this);
  }
}
