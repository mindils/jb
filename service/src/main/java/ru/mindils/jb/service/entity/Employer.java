package ru.mindils.jb.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "employer")
public class Employer {

  /**
   * Уникальный ключ из внешней системы. Самостоятельно не генерируется
   */
  @Id
  private String id;
  private String name;
  private Boolean trusted;
  private String description;
  private Boolean is_detailed;
  private LocalDateTime localCreatedAt;
  private LocalDateTime localModifiedAt;
}
