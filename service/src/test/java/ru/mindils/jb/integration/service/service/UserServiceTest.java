package ru.mindils.jb.integration.service.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.integration.service.ITBase;
import ru.mindils.jb.service.dto.RegistrationDto;
import ru.mindils.jb.service.dto.UserReadDto;
import ru.mindils.jb.service.dto.UserUpdateDto;
import ru.mindils.jb.service.service.UserService;

@RequiredArgsConstructor
class UserServiceTest extends ITBase {

  private static final Long USER_ID_1 = 2L;
  private static final Long ADMIN_ID_1 = 2L;
  private final UserService userService;

  @Test
  void findById() {
    Optional<UserReadDto> user1 = userService.findById(USER_ID_1);
    assertThat(user1).isPresent();
  }

  @Test
  void create() {
    UserReadDto user = userService.create(getUserCreateDto());
    assertThat(user).isNotNull();
  }

  @Test
  void update() {
    Optional<UserReadDto> update = userService.update(ADMIN_ID_1, getUserUpdateDto());
    assertThat(update).isPresent();

    assertThat(update.get().username()).isEqualTo("john");
    assertThat(update.get().role()).isEqualTo("USER");
  }

  @Test
  void delete() {
    assertThat(userService.delete(USER_ID_1)).isTrue();
    assertThat(userService.delete(10000000L)).isFalse();
  }

  private RegistrationDto getUserCreateDto() {
    return RegistrationDto.builder()
        .email("example@mail.com")
        .firstname("john")
        .password("123")
        .confirmPassword("123")
        .username("john")
        .build();
  }

  private UserUpdateDto getUserUpdateDto() {
    return new UserUpdateDto("john", "USER");
  }
}
