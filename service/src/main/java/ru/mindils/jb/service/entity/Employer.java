package ru.mindils.jb.service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString(exclude = {"vacancy", "employerInfo"})
@EqualsAndHashCode(exclude = {"vacancy", "employerInfo", "createdAt", "modifiedAt"})
@Table(name = "jb_employer")
public class Employer implements BaseEntity<String> {

  /** Уникальный ключ из внешней системы. Самостоятельно не генерируется */
  @Id
  private String id;

  private String name;

  @Builder.Default
  private Boolean trusted = false;

  private String description;
  private Boolean detailed;

  @Builder.Default
  @OneToMany(mappedBy = "employer")
  @JsonBackReference
  private List<Vacancy> vacancy = new ArrayList<>();

  @OneToOne(mappedBy = "employer", fetch = FetchType.LAZY)
  private EmployerInfo employerInfo;

  private Instant createdAt;
  private Instant modifiedAt;

  @PrePersist
  public void prePersist() {
    createdAt = Instant.now();
  }

  @PreUpdate
  public void preUpdate() {
    modifiedAt = Instant.now();
  }
}
