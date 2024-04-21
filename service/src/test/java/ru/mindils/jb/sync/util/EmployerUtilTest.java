package ru.mindils.jb.sync.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ru.mindils.jb.service.entity.Employer;

class EmployerUtilTest {

  @Test
  void getEmployerEmpty_withDefaultId() {
    Employer result = EmployerUtil.getEmployerEmpty();

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("0");
    assertThat(result.getName()).isEqualTo("<EMPTY>");
    assertThat(result.getTrusted()).isFalse();
    assertThat(result.getDescription()).isEmpty();
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getModifiedAt()).isNull();
    assertThat(result.getDetailed()).isTrue();
  }

  @Test
  void getEmployerEmpty_withCustomId() {
    String customId = "custom-id";

    Employer result = EmployerUtil.getEmployerEmpty(customId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(customId);
    assertThat(result.getName()).isEqualTo("<EMPTY>");
    assertThat(result.getTrusted()).isFalse();
    assertThat(result.getDescription()).isEmpty();
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getModifiedAt()).isNull();
    assertThat(result.getDetailed()).isTrue();
  }
}
