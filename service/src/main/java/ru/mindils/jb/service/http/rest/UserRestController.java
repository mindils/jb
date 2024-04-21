package ru.mindils.jb.service.http.rest;

import static org.springframework.http.ResponseEntity.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.mindils.jb.service.dto.RegistrationDto;
import ru.mindils.jb.service.dto.UserReadDto;
import ru.mindils.jb.service.dto.UserUpdateDto;
import ru.mindils.jb.service.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestController {

  private final UserService userService;

  @GetMapping("/{id}")
  public ResponseEntity<UserReadDto> findById(@PathVariable Long id) {
    return userService
        .findById(id)
        .map(user -> ResponseEntity.ok().body(user))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserReadDto create(@RequestBody RegistrationDto userDto) {
    return userService.create(userDto);
  }

  @PutMapping("{id}")
  public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserUpdateDto updateUserDto) {
    return userService
        .update(id, updateUserDto)
        .map(user -> ResponseEntity.ok().body(user)) // Возвращает обновленного пользователя
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "User with ID " + id + " not found")); // Пользователь не найден
  }

  @GetMapping("{username}/avatar")
  public byte[] findAvatar(@PathVariable String username) {
    return userService.findAvatar(username);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<?> deleteUser(@PathVariable Long id) {
    return userService.delete(id) ? noContent().build() : notFound().build();
  }
}
