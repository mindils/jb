package ru.mindils.jb.service.entity;

import jakarta.persistence.Entity;
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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(exclude = "params")
@EqualsAndHashCode(exclude = "params")
@Table(name = "jb_vacancy_filter")
public class VacancyFilter implements BaseEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String code;
  private String name;

  @Builder.Default
  @OneToMany(mappedBy = "vacancyFilter")
  private List<VacancyFilterParams> params = new ArrayList<>();

  private Instant createdAt;
  private Instant modifiedAt;

  public void addParam(VacancyFilterParams param) {
    params.add(param);
    param.setVacancyFilter(this);
  }
}
