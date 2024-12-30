package org.tu.sofia.java.questionnaire.unit.service;

import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;
import org.tu.sofia.java.questionnaire.services.AuthenticationService;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@NoArgsConstructor
public class AuthenticationServiceTests {

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_PASSWORD = "testPassword";

    private UserEntity getTestUserEntity() {
        return new UserEntity(1L, TEST_USERNAME, TEST_PASSWORD, new HashSet<>(), new HashSet<>());
    }

    @Test
    public void registerUserSuccess() {
        // Mock the "save" method of the repository
        when(authenticationRepository.save(any())).thenReturn(getTestUserEntity());

        // Call the "attemptRegister" method of the service
        final String token = authenticationService.attemptRegister(TEST_USERNAME, TEST_PASSWORD);

        // Assert that a token was returned
        assertNotNull(token);
    }

    @Test
    public void registerUserFailBlankUsernameAndPassword() {
        // Assert that "IllegalArgumentException" is thrown
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authenticationService.attemptRegister("", ""));

        // Assert exception message
        assertEquals("Username and password must not be empty.", exception.getMessage());
    }

    @Test
    public void registerUserFailUsernameAlreadyTaken() {
        // Mock the "save" method of the authentication repository
        when(authenticationRepository.save(any())).thenThrow(new DataIntegrityViolationException(""));

        // Assert that "RuntimeException" is thrown
        final RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authenticationService.attemptRegister(TEST_USERNAME, TEST_PASSWORD));

        // Assert exception message
        assertEquals("Username is already taken.", exception.getMessage());
    }

    @Test
    public void loginUserSuccess() {
        // Mock the "authenticate" method of the authentication manager (return value doesn't matter)
        when(authenticationManager.authenticate(any())).thenReturn(null);

        // Mock the "findByUsername" method of the authentication repository
        when(authenticationRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(getTestUserEntity()));

        // Call the "attemptLogin" method of the service
        final String token = authenticationService.attemptLogin(TEST_USERNAME, TEST_PASSWORD);

        // Assert that a token was returned
        assertNotNull(token);
    }

    @Test
    public void loginUserFailBlankUsernameAndPassword() {
        // Assert that "IllegalArgumentException" is thrown
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authenticationService.attemptLogin("", ""));

        // Assert exception message
        assertEquals("Username and password must not be empty.", exception.getMessage());
    }

    @Test
    public void loginUserFailInvalidCredentials() {
        // Mock the "authenticate" method of the authentication manager
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException(""));

        // Assert that "RuntimeException" is thrown
        final RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authenticationService.attemptLogin("invalidUsername", "invalidPassword"));

        // Assert exception message
        assertEquals("Invalid username or password.", exception.getMessage());
    }
}
