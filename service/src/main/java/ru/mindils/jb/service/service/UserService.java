package ru.mindils.jb.service.service;

import static io.micrometer.common.util.StringUtils.isNotEmpty;

import jakarta.validation.Valid;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.mindils.jb.service.dto.RegistrationDto;
import ru.mindils.jb.service.dto.UserReadDto;
import ru.mindils.jb.service.dto.UserUpdateDto;
import ru.mindils.jb.service.entity.User;
import ru.mindils.jb.service.mapper.UserMapper;
import ru.mindils.jb.service.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final UserImageService imageService;

  public Optional<UserReadDto> findById(Long id) {
    return userRepository.findById(id).map(userMapper::map);
  }

  public UserReadDto findByUsername(String username) {
    return userRepository.findByUsername(username).map(userMapper::map).orElseThrow();
  }

  public List<UserReadDto> findAll() {
    return userRepository.findAll().stream().map(userMapper::map).toList();
  }

  @Transactional
  public UserReadDto create(RegistrationDto userDto) {
    return Optional.of(userDto)
        .map(dto -> User.builder()
            .username(dto.getUsername())
            .firstname(dto.getFirstname())
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .enabled(false)
            .role("USER")
            .build())
        .map(userRepository::save)
        .map(userMapper::map)
        .orElseThrow();
  }

  @Transactional
  public Optional<UserReadDto> updateProfile(String username, @Valid UserUpdateDto updateUserDto) {
    return userRepository
        .findByUsername(username)
        .map(entity -> {
          var newUser = userMapper.map(updateUserDto, entity);

          if (!updateUserDto.getImage().isEmpty()) {
            uploadImage(updateUserDto.getImage());
            newUser.setImage(updateUserDto.getImage().getOriginalFilename());
          }

          return newUser;
        })
        .map(userRepository::saveAndFlush)
        .map(userMapper::map);
  }

  public byte[] findAvatar(String username) {
    return userRepository
        .findByUsername(username)
        .filter(user -> isNotEmpty(user.getImage()))
        .map(user -> imageService.get(user.getImage()))
        .orElseGet(imageService::getDefault);
  }

  @Transactional
  public Optional<UserReadDto> update(Long id, UserUpdateDto updateUserDto) {
    return userRepository
        .findById(id)
        .map(entity -> userMapper.map(updateUserDto, entity))
        .map(userRepository::saveAndFlush)
        .map(userMapper::map);
  }

  @Transactional
  public Boolean delete(Long id) {
    return userRepository
        .findById(id)
        .map(user -> {
          userRepository.delete(user);
          return true;
        })
        .orElse(false);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .map(user -> org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(List.of(new SimpleGrantedAuthority(user.getRole())))
            .disabled(!user.getEnabled())
            .build())
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }

  @SneakyThrows
  private void uploadImage(MultipartFile image) {
    try (InputStream inputStream = image.getInputStream()) {
      imageService.upload(image.getOriginalFilename(), inputStream);
    }
  }

  @Transactional
  public void toggleUserStatus(Long userId) {
    userRepository.findById(userId).ifPresent(user -> {
      user.setEnabled(!user.getEnabled());
      userRepository.save(user);
    });
  }

  @Transactional
  public void updateUserRole(Long userId, String role) {
    userRepository.findById(userId).ifPresent(user -> {
      user.setRole(role);
      userRepository.save(user);
    });
  }
}
