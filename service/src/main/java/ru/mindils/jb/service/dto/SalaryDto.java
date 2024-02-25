package ru.mindils.jb.service.dto;

import lombok.Data;

@Data
public class SalaryDto {

  private Integer from;
  private Integer to;
  private String currency;
  private Boolean gross;

}
