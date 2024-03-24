package ru.mindils.jb.integration.service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.mindils.jb.integration.service.ITBase;
import ru.mindils.jb.service.entity.User;
import ru.mindils.jb.service.repository.UserRepository;

@RequiredArgsConstructor
public class UserRepositoryIT extends ITBase {

    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Test
    void save() {
        User user = getUser();
        userRepository.save(user);

        assertThat(user.getId()).isNotNull();
    }

    @Test
    void findById() {
        User user = getUser();
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        Optional<User> actualResult = userRepository.findById(user.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(user);
    }

    @Test
    void update() {
        User user = getUser();
        userRepository.save(user);
        entityManager.flush();

        user.setUsername("newUsername");
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        Optional<User> actualResult = userRepository.findById(user.getId());

        assertThat(actualResult.isPresent()).isTrue();
        assertThat(actualResult.get()).isEqualTo(user);
    }

    @Test
    void delete() {
        User user = getUser();
        userRepository.save(user);
        entityManager.flush();

        userRepository.delete(user);
        entityManager.flush();
        entityManager.clear();

        Optional<User> actualResult = userRepository.findById(user.getId());
        assertThat(actualResult.isPresent()).isFalse();
    }

    private User getUser() {
        return User.builder()
                .username("mindils")
                .role("ADMIN")
                .password("{noop}pass")
                .modifiedAt(Instant.now())
                .createdAt(Instant.now())
                .build();
    }
}
