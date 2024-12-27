package org.tu.sofia.java.questionnaire.controllers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.tu.sofia.java.questionnaire.schemas.ErrorResponseSchema;
import org.tu.sofia.java.questionnaire.schemas.JwtRequestSchema;
import org.tu.sofia.java.questionnaire.schemas.JwtResponseSchema;
import org.tu.sofia.java.questionnaire.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/auth", consumes = "application/json", produces = "application/json")
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(value = "/login")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Successful login.",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = JwtResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Invalid username or password.",
                    responseCode = "403",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> login(@RequestBody JwtRequestSchema request) {
        try {
            // Attempt login
            final String token = authenticationService.attemptLogin(request.getUsername(), request.getPassword());

            // Return 200 response with token
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new JwtResponseSchema(token, request.getUsername()));

        } catch (RuntimeException e) {
            // Return 403 response, most likely invalid credentials
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseSchema(
                            DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                            HttpStatus.FORBIDDEN.value(),
                            e.getMessage(),
                            "/api/auth/login",
                            HttpMethod.POST.name()
                    ));
        }
    }

    @PostMapping(value = "/register")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Successful register.",
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = JwtResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Username already taken",
                    responseCode = "403",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> register(@RequestBody JwtRequestSchema request) {
        try {
            // Attempt register
            final String token = authenticationService.attemptRegister(request.getUsername(), request.getPassword());

            // Return 201 response with token
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new JwtResponseSchema(token, request.getUsername()));

        } catch (RuntimeException e) {
            // Return error response, most likely duplicate username
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseSchema(
                            DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                            HttpStatus.FORBIDDEN.value(),
                            e.getMessage(),
                            "/api/auth/register",
                            HttpMethod.POST.name()
                    ));
        }
    }
}