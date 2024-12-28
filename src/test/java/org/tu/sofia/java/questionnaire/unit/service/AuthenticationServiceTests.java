package org.tu.sofia.java.questionnaire.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;
import org.tu.sofia.java.questionnaire.services.AuthenticationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class AuthenticationServiceTests {

    @MockitoBean
    public AuthenticationRepository authenticationRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private static final String testUsername = "testUsername";
    private static final String testPassword = "testPassword";

    @Test
    public void registerUserSuccess() {
        // Call the "attemptRegister" method of the service
        final String token = authenticationService.attemptRegister(testUsername, testPassword);

        // Verify that the "saveAndFlush" method of the authentication repository was called only once
        verify(authenticationRepository, times(1)).saveAndFlush(any(UserEntity.class));

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
}
