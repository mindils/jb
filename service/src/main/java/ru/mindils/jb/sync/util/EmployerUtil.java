package ru.mindils.jb.sync.util;

import lombok.experimental.UtilityClass;
import ru.mindils.jb.service.entity.Employer;

@UtilityClass
public class EmployerUtil {

  public static Employer getEmployerEmpty() {
    return getEmployerEmpty("0");
  }

  public static Employer getEmployerEmpty(String id) {
    return Employer.builder()
        .id(id)
        .name("<EMPTY>")
        .trusted(false)
        .description("")
        .createdAt(null)
        .modifiedAt(null)
        .detailed(true)
        .build();
  }
}
