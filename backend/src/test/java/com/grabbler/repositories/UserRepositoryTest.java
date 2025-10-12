package com.grabbler.repositories;

import com.grabbler.models.Address;
import com.grabbler.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan(basePackages = "com.grabbler.models")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
    }

    @Test
    public void whenFindByEmail_thenReturnUser() {
        // given
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void whenFindByAddress_thenReturnUserList() {
        // given
        Address address = new Address("Germany", "Munich", "80331", "Marienplatz 1", null);
        entityManager.persist(address);
        user.setAddresses(Collections.singletonList(address));
        entityManager.persist(user);
        entityManager.flush();

        // when
        List<User> users = userRepository.findByAddress(address.getAddressId());

        // then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getAddresses().get(0).getCity()).isEqualTo("Munich");
    }
}
