package ru.mindils.jb.service.service;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserImageService {

  private final String bucket;
  private final Path defaultAvatar; // =

  public UserImageService(
      @Value("${app.image.buckets}") String bucket,
      @Value("${app.image.avatar.default}") String defaultAvatar) {
    this.bucket = bucket;
    this.defaultAvatar = Path.of(defaultAvatar);
  }

  @SneakyThrows
  public void upload(String path, InputStream content) {
    Path fullImagePath = Path.of(bucket, path);

    Files.createDirectories(fullImagePath.getParent());
    Files.write(fullImagePath, content.readAllBytes(), CREATE, TRUNCATE_EXISTING);
  }

  @SneakyThrows
  public byte[] get(String path) {
    if (path == null) {
      return getDefault();
    }

    Path fullImagePath = Path.of(bucket, path);

    return Files.exists(fullImagePath) ? Files.readAllBytes(fullImagePath) : getDefault();
  }

  @SneakyThrows
  public byte[] getDefault() {
    return Files.readAllBytes(defaultAvatar);
  }
}
