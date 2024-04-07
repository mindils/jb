package ru.mindils.jb.service.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindils.jb.service.dto.UserCreateDto;
import ru.mindils.jb.service.dto.UserReadDto;
import ru.mindils.jb.service.dto.UserUpdateDto;
import ru.mindils.jb.service.mapper.UserMapper;
import ru.mindils.jb.service.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public Optional<UserReadDto> findById(Long id) {
    return userRepository.findById(id).map(userMapper::map);
  }

  @Transactional
  public UserReadDto create(UserCreateDto user) {
    return Optional.of(user)
        .map(userMapper::map)
        .map(userRepository::save)
        .map(userMapper::map)
        .orElseThrow();
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
}
