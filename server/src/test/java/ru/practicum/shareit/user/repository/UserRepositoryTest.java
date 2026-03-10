package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("User");

        userRepository.save(user);

        Optional<User> saveUser = userRepository.findByEmail("test@test.com");

        assertThat(saveUser).isPresent();
        assertThat(saveUser.get().getEmail()).isEqualTo("test@test.com");
        assertThat(saveUser.get().getName()).isEqualTo("User");
    }

    @Test
    void findByEmailNotFound() {
        Optional<User> saveUser = userRepository.findByEmail("est@test.com");

        assertThat(saveUser).isEmpty();
    }
}