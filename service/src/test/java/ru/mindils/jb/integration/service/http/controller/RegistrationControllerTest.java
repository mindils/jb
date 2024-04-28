package ru.mindils.jb.integration.service.http.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import org.springframework.test.web.servlet.MockMvc;
import ru.mindils.jb.integration.ITBase;
import ru.mindils.jb.service.dto.RegistrationDto;
import ru.mindils.jb.service.service.UserService;

@AutoConfigureMockMvc
@RequiredArgsConstructor
public class RegistrationControllerTest extends ITBase {

  private final MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  void showRegistration() throws Exception {
    mockMvc
        .perform(get("/registration"))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/registration"))
        .andExpect(model().attributeExists("registrationDto"));
  }

  @Test
  void registration_success() throws Exception {
    RegistrationDto registrationDto = RegistrationDto.builder()
        .username("testuser")
        .firstname("John")
        .password("password")
        .confirmPassword("password")
        .email("test@example.com")
        .build();

    mockMvc
        .perform(post("/registration").with(csrf()).flashAttr("registrationDto", registrationDto))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/login?registration"));

    verify(userService).create(registrationDto);
  }

  @Test
  void registration_validationError() throws Exception {
    RegistrationDto registrationDto = RegistrationDto.builder()
        .username("")
        .firstname("")
        .password("")
        .confirmPassword("")
        .email("invalid-email")
        .build();

    mockMvc
        .perform(post("/registration").with(csrf()).flashAttr("registrationDto", registrationDto))
        .andExpect(status().isOk())
        .andExpect(view().name("pages/registration"))
        .andExpect(model().attributeHasFieldErrors("registrationDto", "username"))
        .andExpect(model().attributeHasFieldErrors("registrationDto", "firstname"))
        .andExpect(model().attributeHasFieldErrors("registrationDto", "password"))
        .andExpect(model().attributeHasFieldErrors("registrationDto", "confirmPassword"))
        .andExpect(model().attributeHasFieldErrors("registrationDto", "email"));

    verify(userService, never()).create(any());
  }
}
