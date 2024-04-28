package ru.mindils.jb.integration.service.http.controller;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.dto.UserReadDto;
import ru.mindils.jb.service.service.UserService;

@AutoConfigureMockMvc
@RequiredArgsConstructor
public class UserControllerTest extends ITBase {

  private final MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  @WithMockUser(username = "testuser", authorities = "USER")
  void profile() throws Exception {
    UserReadDto userReadDto =
        new UserReadDto(1L, "testuser", "USER", "test@example.com", "John", true);
    when(userService.findByUsername("testuser")).thenReturn(userReadDto);

    mockMvc
        .perform(get("/user/profile"))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/user.profile"))
        .andExpect(model().attribute("user", userReadDto));

    verify(userService).findByUsername("testuser");
  }

  @Test
  @WithMockUser(username = "user", authorities = "USER")
  void updateProfile() throws Exception {
    mockMvc
        .perform(post("/user/profile")
            .with(csrf())
            .param("email", "test@example.com")
            .param("firstname", "John"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/user/profile"));

    verify(userService)
        .updateProfile(
            eq("user"),
            argThat(dto ->
                dto.getEmail().equals("test@example.com") && dto.getFirstname().equals("John")));
  }
}
