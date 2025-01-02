package org.tu.sofia.java.questionnaire.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.*; // NOPMD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.tu.sofia.java.questionnaire.config.JwtRequestFilter;
import org.tu.sofia.java.questionnaire.controllers.AuthenticationController;
import org.tu.sofia.java.questionnaire.schemas.JwtRequestSchema;
import org.tu.sofia.java.questionnaire.services.AuthenticationService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@NoArgsConstructor
public class AuthenticationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;

    @MockitoBean
    private AuthenticationService authenticationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${unit.test.username}")
    private String testUsername;

    @Value("${unit.test.password}")
    private String testPassword;

    @Nested
    @NoArgsConstructor
    public class Login {

        @Test
        public void success() throws Exception {
            // Init a token for the "attemptLogin" return value
            final String token = "anyTokenIsValid";

            // Init a valid JWTRequest
            final JwtRequestSchema request = new JwtRequestSchema(testUsername, testPassword);

            // Mock the "attemptLogin" method of the authentication service
            doReturn(token).when(authenticationService).attemptLogin(testUsername, testPassword);

            // Perform POST request to /api/auth/login
            mockMvc.perform(post("/api/auth/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"username\":\"%s\"".formatted(testUsername))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"token\":\"%s\"".formatted(token))));
        }

        @Test
        public void failEmptyUsernameAndPassword() throws Exception {
            // Init a valid JWTRequest
            final JwtRequestSchema request = new JwtRequestSchema("", "");

            // Mock the "attemptLogin" method of the authentication service
            doThrow(IllegalArgumentException.class).when(authenticationService).attemptLogin("", "");

            // Perform POST request to /api/auth/login
            mockMvc.perform(post("/api/auth/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        public void failInvalidCredentials() throws Exception {
            // Init a valid JWTRequest
            final JwtRequestSchema request = new JwtRequestSchema(testUsername, testPassword);

            // Mock the "attemptLogin" method of the authentication service
            doThrow(BadCredentialsException.class).when(authenticationService).attemptLogin(testUsername, testPassword);

            // Perform POST request to /api/auth/login
            mockMvc.perform(post("/api/auth/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }

    @Nested
    @NoArgsConstructor
    public class Register {

        @Test
        public void success() throws Exception {
            // Init a token for the "attemptRegister" return value
            final String token = "anyTokenIsValid";

            // Init a valid JWTRequest
            final JwtRequestSchema request = new JwtRequestSchema(testUsername, testPassword);

            // Mock the "attemptRegister" method of the authentication service
            doReturn(token).when(authenticationService).attemptRegister(testUsername, testPassword);

            // Perform POST request to /api/auth/register
            mockMvc.perform(post("/api/auth/register")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"username\":\"%s\"".formatted(testUsername))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"token\":\"%s\"".formatted(token))));
        }

        @Test
        public void failEmptyUsernameAndPassword() throws Exception {
            // Init a valid JWTRequest
            final JwtRequestSchema request = new JwtRequestSchema("", "");

            // Mock the "attemptRegister" method of the authentication service
            doThrow(IllegalArgumentException.class)
                    .when(authenticationService).attemptRegister("", "");

            // Perform POST request to /api/auth/register
            mockMvc.perform(post("/api/auth/register")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        public void failInvalidCredentials() throws Exception {
            // Init a valid JWTRequest
            final JwtRequestSchema request = new JwtRequestSchema(testUsername, testPassword);

            // Mock the "attemptRegister" method of the authentication service
            doThrow(DataIntegrityViolationException.class)
                    .when(authenticationService).attemptRegister(testUsername, testPassword);

            // Perform POST request to /api/auth/register
            mockMvc.perform(post("/api/auth/register")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }
}
