package org.tu.sofia.java.questionnaire.unit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;
import org.tu.sofia.java.questionnaire.services.AuthenticationService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthenticationServiceTests {

    @Autowired
    public AuthenticationRepository authenticationRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private static final String testUsername = "testUsername";
    private static final String testPassword = "testPassword";

    @Test
    public void registerUserSuccess() {
        // Call the "attemptRegister" method of the service
        final String token = authenticationService.attemptRegister(testUsername, testPassword);

        // Get that a user was created
        Optional<UserEntity> optionalUser = authenticationRepository.findByUsername(testUsername);
        assertTrue(optionalUser.isPresent());

        // Assert that a token was returned
        assertNotNull(token);
    }

    @Test
    public void registerUserFailBlankUsernameAndPassword() {
        // Assert that "IllegalArgumentException" is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authenticationService.attemptRegister("", ""));

        // Assert exception message
        assertEquals("Username and password must not be empty.", exception.getMessage());
    }

    @Test
    @Sql("classpath:database-scripts/create-user.sql")
    public void registerUserFailUsernameAlreadyTaken() {
        // Assert that "RuntimeException" is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authenticationService.attemptRegister(testUsername, testPassword));

        // Assert exception message
        assertEquals("Username is already taken.", exception.getMessage());
    }
}
