package ru.mindils.jb.sync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailedEmployerDto {

  private String id;
  private String name;
  private String description;

  private Boolean detailed = true;
}
