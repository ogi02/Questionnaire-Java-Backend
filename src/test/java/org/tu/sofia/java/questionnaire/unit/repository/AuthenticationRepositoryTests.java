package org.tu.sofia.java.questionnaire.unit.repository;

import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@NoArgsConstructor
public class AuthenticationRepositoryTests {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Value("${unit.test.username}")
    private String testUsername;

    @Value("${unit.test.password}")
    private String testPassword;

    @Test
    @Transactional
    @Rollback
    public void testSaveUser() {
        // Create a user with the test data
        final UserEntity user = new UserEntity(testUsername, testPassword);

        // Save the user
        final UserEntity savedUser = authenticationRepository.save(user);

        // Assert
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(testUsername, savedUser.getUsername());
        assertEquals(testPassword, savedUser.getPassword());
    }

    @Test
    @Transactional
    @Rollback
    public void testFindByUsernameUserFound() {
        // Create a user with the test data
        final UserEntity user = new UserEntity(testUsername, testPassword);

        // Save the user
        authenticationRepository.save(user);

        // Find the user by username
        final Optional<UserEntity> optionalUser = authenticationRepository.findByUsername(testUsername);

        // Assert
        assertTrue(optionalUser.isPresent());
        final UserEntity foundUser = optionalUser.get();
        assertNotNull(foundUser.getId());
        assertEquals(testUsername ,foundUser.getUsername());
        assertEquals(testPassword ,foundUser.getPassword());
    }

    @Test
    @Transactional
    @Rollback
    public void testFindByUsernameUserNotFound() {
        // Find a non-existent user
        final Optional<UserEntity> optionalUser = authenticationRepository.findByUsername("nonExistentUsername");

        // Assert
        assertTrue(optionalUser.isEmpty());
    }
}
