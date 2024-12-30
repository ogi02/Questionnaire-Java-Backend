package org.tu.sofia.java.questionnaire.unit.service;

import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;
import org.tu.sofia.java.questionnaire.services.AuthenticationService;
import org.tu.sofia.java.questionnaire.unit.creators.UserCreator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@NoArgsConstructor
public class AuthenticationServiceTests {

    @MockitoBean
    protected AuthenticationManager authenticationManager;

    @MockitoBean
    protected AuthenticationRepository authenticationRepository;

    @Autowired
    protected AuthenticationService authenticationService;

    @Value("${unit.test.username}")
    protected String testUsername;

    @Value("${unit.test.password}")
    protected String testPassword;

    @Nested
    @NoArgsConstructor
    public class AttemptRegister {
        @Test
        public void success() {
            // Init test user
            final UserEntity testUser = UserCreator.createEntity();

            // Mock the "save" method of the repository
            doReturn(testUser).when(authenticationRepository).save(any());

            // Call the "attemptRegister" method of the service
            final String token = authenticationService.attemptRegister(testUsername, testPassword);

            // Verify that the "save" method was called
            verify(authenticationRepository, times(1)).save(any());

            // Assert that a token was returned
            assertNotNull(token);
        }

        @Test
        public void failBlankUsernameAndPassword() {
            // Assert that "IllegalArgumentException" is thrown
            final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    authenticationService.attemptRegister("", ""));

            // Assert exception message
            assertEquals("Username and password must not be empty.", exception.getMessage());
        }

        @Test
        public void failUsernameAlreadyTaken() {
            // Mock the "save" method of the authentication repository
            doThrow(new DataIntegrityViolationException(""))
                    .when(authenticationRepository).save(any());

            // Assert that "RuntimeException" is thrown
            final RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    authenticationService.attemptRegister(testUsername, testPassword));

            // Verify that the "save" method was called
            verify(authenticationRepository, times(1)).save(any());

            // Assert exception message
            assertEquals("Username is already taken.", exception.getMessage());
        }
    }

    @Nested
    @NoArgsConstructor
    public class AttemptLogin {

        @Test
        public void success() {
            // Init test user
            final UserEntity testUser = UserCreator.createEntity();

            // Mock the "authenticate" method of the authentication manager (return value doesn't matter)
            doReturn(null).when(authenticationManager).authenticate(any());

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(testUser)).when(authenticationRepository).findByUsername(testUsername);

            // Call the "attemptLogin" method of the service
            final String token = authenticationService.attemptLogin(testUsername, testPassword);

            // Verify that the "findByUsername" method was called
            verify(authenticationRepository, times(1)).findByUsername(testUser.getUsername());

            // Assert that a token was returned
            assertNotNull(token);
        }

        @Test
        public void failBlankUsernameAndPassword() {
            // Assert that "IllegalArgumentException" is thrown
            final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    authenticationService.attemptLogin("", ""));

            // Assert exception message
            assertEquals("Username and password must not be empty.", exception.getMessage());
        }

        @Test
        public void failInvalidCredentials() {
            // Mock the "authenticate" method of the authentication manager
            when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException(""));

            // Assert that "RuntimeException" is thrown
            final RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    authenticationService.attemptLogin("invalidUsername", "invalidPassword"));

            // Assert exception message
            assertEquals("Invalid username or password.", exception.getMessage());
        }
    }
}
