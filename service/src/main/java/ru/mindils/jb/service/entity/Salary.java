package ru.mindils.jb.service.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class Salary {

  private Boolean salaryGross;
  private Integer salaryFrom;
  private Integer salaryTo;
  private String salaryCurrency;
}
