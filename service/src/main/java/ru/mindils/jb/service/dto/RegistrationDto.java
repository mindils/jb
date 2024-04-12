package ru.mindils.jb.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationDto {

  @NotBlank(message = "Логин обязателен")
  @Size(min = 3, message = "Логин должен быть не менее 3 символов")
  private String username;

  @NotBlank(message = "Имя обязательно")
  @Size(min = 3, message = "Имя должен быть не менее 3 символов")
  private String firstname;

  @NotBlank(message = "Пароль обязателен")
  private String password;

  @NotBlank(message = "Подтверждение пароля обязательно")
  private String confirmPassword;

  @NotBlank(message = "Электронная почта обязательна")
  @Email(message = "Неверный формат электронной почты")
  private String email;
}
