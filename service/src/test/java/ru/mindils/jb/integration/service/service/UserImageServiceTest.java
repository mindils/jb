package ru.mindils.jb.integration.service.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import ru.mindils.jb.service.service.UserImageService;

class UserImageServiceTest {

  @TempDir
  Path tempDir;

  @Mock
  private Path defaultAvatar;

  private UserImageService userImageService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userImageService = new UserImageService(tempDir.toString(), defaultAvatar.toString());
  }

  @Test
  void upload_shouldSaveImageFile() throws IOException {
    String imagePath = "test.jpg";
    byte[] imageContent = "test image".getBytes();

    userImageService.upload(imagePath, new ByteArrayInputStream(imageContent));

    Path savedImagePath = tempDir.resolve(imagePath);
    assertThat(savedImagePath).exists();
    assertThat(Files.readAllBytes(savedImagePath)).isEqualTo(imageContent);
  }

  @Test
  void get_shouldReturnImageBytes() throws IOException {
    String imagePath = "test.jpg";
    byte[] imageContent = "test image".getBytes();
    Path imageFile = tempDir.resolve(imagePath);
    Files.write(imageFile, imageContent);

    byte[] result = userImageService.get(imagePath);

    assertThat(result).isEqualTo(imageContent);
  }

  @Test
  void getDefault_shouldReturnDefaultAvatarBytes() throws IOException {
    byte[] defaultAvatarContent = "default avatar".getBytes();
    ReflectionTestUtils.setField(userImageService, "defaultAvatar", tempDir.resolve("default.jpg"));
    Files.write(tempDir.resolve("default.jpg"), defaultAvatarContent);

    byte[] result = userImageService.getDefault();

    assertThat(result).isEqualTo(defaultAvatarContent);
  }
}
