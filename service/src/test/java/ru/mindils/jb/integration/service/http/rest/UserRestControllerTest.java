package ru.mindils.jb.integration.service.http.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.dto.RegistrationDto;
import ru.mindils.jb.service.dto.UserReadDto;
import ru.mindils.jb.service.dto.UserUpdateDto;
import ru.mindils.jb.service.service.UserService;

@AutoConfigureMockMvc
@RequiredArgsConstructor
class UserRestControllerTest extends ITBase {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @Test
  void findById_success() throws Exception {
    Long userId = 1L;
    UserReadDto userReadDto =
        new UserReadDto(userId, "testuser", "USER", "test@example.com", "John", true);
    when(userService.findById(userId)).thenReturn(Optional.of(userReadDto));

    String response = mockMvc
        .perform(get("/api/v1/users/{id}", userId))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    UserReadDto actualUserReadDto = objectMapper.readValue(response, UserReadDto.class);
    assertThat(actualUserReadDto).isEqualTo(userReadDto);
  }

  @Test
  void findById_notFound() throws Exception {
    Long userId = 1L;
    when(userService.findById(userId)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/v1/users/{id}", userId)).andExpect(status().isNotFound());
  }

  @Test
  void create_success() throws Exception {
    RegistrationDto registrationDto = RegistrationDto.builder()
        .username("testuser")
        .firstname("John")
        .password("password")
        .confirmPassword("password")
        .email("test@example.com")
        .build();
    UserReadDto userReadDto =
        new UserReadDto(1L, "testuser", "USER", "test@example.com", "John", true);
    when(userService.create(registrationDto)).thenReturn(userReadDto);

    String response = mockMvc
        .perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registrationDto)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    UserReadDto actualUserReadDto = objectMapper.readValue(response, UserReadDto.class);
    assertThat(actualUserReadDto).isEqualTo(userReadDto);
  }

  @Test
  void update_success() throws Exception {
    Long userId = 1L;
    UserUpdateDto userUpdateDto = new UserUpdateDto("test@example.com", "John", null);
    UserReadDto userReadDto =
        new UserReadDto(userId, "testuser", "USER", "test@example.com", "John", true);
    when(userService.update(eq(userId), any(UserUpdateDto.class)))
        .thenReturn(Optional.of(userReadDto));

    String response = mockMvc
        .perform(put("/api/v1/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userUpdateDto)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    UserReadDto actualUserReadDto = objectMapper.readValue(response, UserReadDto.class);
    assertThat(actualUserReadDto).isEqualTo(userReadDto);
  }

  @Test
  void update_notFound() throws Exception {
    Long userId = 1L;
    UserUpdateDto userUpdateDto = new UserUpdateDto("test@example.com", "John", null);
    when(userService.update(eq(userId), any(UserUpdateDto.class))).thenReturn(Optional.empty());

    mockMvc
        .perform(put("/api/v1/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userUpdateDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteUser_success() throws Exception {
    Long userId = 1L;
    when(userService.delete(userId)).thenReturn(true);

    mockMvc.perform(delete("/api/v1/users/{id}", userId)).andExpect(status().isNoContent());
  }

  @Test
  void deleteUser_notFound() throws Exception {
    Long userId = 1L;
    when(userService.delete(userId)).thenReturn(false);

    mockMvc.perform(delete("/api/v1/users/{id}", userId)).andExpect(status().isNotFound());
  }

  @Test
  void findAvatar_success() throws Exception {
    String username = "testuser";
    byte[] avatarBytes = "avatar".getBytes();
    when(userService.findAvatar(username)).thenReturn(avatarBytes);

    mockMvc
        .perform(get("/api/v1/users/{username}/avatar", username))
        .andExpect(status().isOk())
        .andExpect(content().bytes(avatarBytes));
  }
}
