package org.tu.sofia.java.questionnaire.unit.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AuthenticationRepositoryTests {

    private AuthenticationRepository authenticationRepository;

    @Autowired
    public void setAuthenticationRepository(AuthenticationRepository authenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    @Test
    @Transactional
    @Rollback
    public void testSaveUser() {
        // Define test data
        String username = "testUsername";
        String password = "testPassword";

        // Create a user with the test data
        UserEntity user = new UserEntity(username, password);

        // Save the user
        UserEntity savedUser = authenticationRepository.save(user);

        // Assert
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(savedUser.getUsername(), username);
        assertEquals(savedUser.getPassword(), password);
    }

    @Test
    @Transactional
    @Rollback
    public void testFindByUsernameUserFound() {
        // Define test data
        String username = "testUsername";
        String password = "testPassword";

        // Create a user with the test data
        UserEntity user = new UserEntity(username, password);

        // Save the user
        authenticationRepository.save(user);

        // Find the user by username
        Optional<UserEntity> optionalUser = authenticationRepository.findByUsername(username);

        // Assert
        assertTrue(optionalUser.isPresent());
        UserEntity foundUser = optionalUser.get();
        assertNotNull(foundUser.getId());
        assertEquals(foundUser.getUsername(), username);
        assertEquals(foundUser.getPassword(), password);
    }

    @Test
    @Transactional
    @Rollback
    public void testFindByUsernameUserNotFound() {
        // Find a non-existent user
        Optional<UserEntity> optionalUser = authenticationRepository.findByUsername("nonExistentUsername");

        // Assert
        assertTrue(optionalUser.isEmpty());
    }
}
