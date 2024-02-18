package ru.mindils.jb.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Employer {

  /**
   * Уникальный ключ из внешней системы. Самостоятельно не генерируется
   */
  @Id
  private String id;
  private String name;
  private Boolean trusted;
  private String description;
  private Boolean detailed;

  @OneToMany(mappedBy = "employer", fetch = FetchType.LAZY)
  private List<Vacancy> vacancy = new ArrayList<>();

  @OneToOne(mappedBy = "employer", fetch = FetchType.LAZY)
  private EmployerInfo employerInfo;

  private Instant createdAt;
  private Instant modifiedAt;
}
