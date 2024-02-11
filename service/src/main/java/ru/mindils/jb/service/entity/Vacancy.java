package ru.mindils.jb.service.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "vacancy")
public class Vacancy {

  /**
   * Уникальный ключ из внешней системы.
   * Самостоятельно не генерируется
   */
  @Id
  private String id;
  private String name;
  private String employerId;
  private Boolean premium;
  private String city;
  private Salary salary;
  private String type;
  private LocalDateTime publishedAt;
  private LocalDateTime createdAt;
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
  private Boolean isDetailed;
  private LocalDateTime localCreatedAt;
  private LocalDateTime localModifiedAt;
}
