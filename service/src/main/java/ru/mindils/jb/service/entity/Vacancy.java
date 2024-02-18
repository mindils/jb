package ru.mindils.jb.service.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Vacancy {

  /**
   * Уникальный ключ из внешней системы. Самостоятельно не генерируется
   */
  @Id
  private String id;
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employer_id")
  private Employer employer;

  private Boolean premium;
  private String city;
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


  /**
   * Список ключевых навыков, получаемых из внешней системы. Пример формата данных:
   * <pre>
   * {
   *   ... other fields ...
   *   "key_skills": [
   *     {"name": "Прием посетителей"},
   *     {"name": "Первичный документооборот"}
   *   ]
   * }
   * </pre>
   * Для упрощения хранения в базе данных, данные преобразуются в строку с помощью mapper.
   */
  private String keySkills;
  private Boolean detailed;

  @OneToOne(mappedBy = "vacancy", fetch = FetchType.LAZY)
  private VacancyInfo vacancyInfo;

  // Временные метки сохранения в вашей системе (createdAt занята и приходит из внешней системы)
  private Instant internalCreatedAt;
  private Instant internalModifiedAt;
}
