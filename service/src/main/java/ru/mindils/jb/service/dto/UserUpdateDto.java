package ru.mindils.jb.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class UserUpdateDto {

  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String firstname;

  private MultipartFile image;
}
