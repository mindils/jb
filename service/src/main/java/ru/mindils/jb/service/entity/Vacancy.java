package ru.mindils.jb.service.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@NamedEntityGraph(
    name = "Vacancy.detail",
    attributeNodes = {
      @NamedAttributeNode(value = "vacancyInfo"),
      @NamedAttributeNode(value = "employer", subgraph = "Employer.detail")
    },
    subgraphs = {
      @NamedSubgraph(
          name = "Employer.detail",
          attributeNodes = {@NamedAttributeNode(value = "employerInfo")})
    })
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"vacancyInfo", "employer"})
@EqualsAndHashCode(exclude = {"vacancyInfo", "employer", "internalCreatedAt", "internalModifiedAt"})
@Data
@Table(name = "jb_vacancy")
public class Vacancy implements BaseEntity<String> {

  /** Уникальный ключ из внешней системы. Самостоятельно не генерируется */
  @Id
  private String id;

  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employer_id")
  private Employer employer;

  private Boolean premium;
  private String city;

  @Embedded
  private Salary salary;

  private String type;
  private Instant publishedAt;
  private Instant createdAt;
  private Boolean archived;
  private String applyAlternateUrl;
  private String url;
  private String alternateUrl;
  private String schedule;
  private String responseUrl;

  @JdbcTypeCode(SqlTypes.JSON)
  private List<Map<String, ?>> professionalRoles;

  private String employment;
  private String description;

  private String keySkills;

  @Builder.Default
  private Boolean detailed = false;

  @OneToOne(mappedBy = "vacancy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private VacancyInfo vacancyInfo;

  // Временные метки сохранения в вашей системе (createdAt занята и приходит из внешней системы)
  private Instant internalCreatedAt;
  private Instant internalModifiedAt;

  @PrePersist
  public void prePersist() {
    internalCreatedAt = Instant.now();
  }

  @PreUpdate
  public void preUpdate() {
    internalModifiedAt = Instant.now();
  }
}
