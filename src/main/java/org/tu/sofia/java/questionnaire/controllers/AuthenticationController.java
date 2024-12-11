package org.tu.sofia.java.questionnaire.controllers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.tu.sofia.java.questionnaire.config.JwtTokenUtil;
import org.tu.sofia.java.questionnaire.schemas.DefaultErrorResponseSchema;
import org.tu.sofia.java.questionnaire.schemas.JwtRequestSchema;
import org.tu.sofia.java.questionnaire.schemas.JwtResponseSchema;
import org.tu.sofia.java.questionnaire.services.AuthenticationService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(value = "/api/auth", consumes = "application/json", produces = "application/json")
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, AuthenticationService authenticationService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationService = authenticationService;
    }

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
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> login(@RequestBody JwtRequestSchema request) {
        try {
            // authenticate with given credentials
            authenticate(request.getUsername(), request.getPassword());

            // get user details
            final UserDetails userDetails = authenticationService.loadUserByUsername(request.getUsername());

            // create token
            final String token = jwtTokenUtil.generateToken(userDetails);

            // return response with status 200
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new JwtResponseSchema(token));

        } catch (Exception e) {
            // return response with status 403
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new DefaultErrorResponseSchema(
                            DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                            HttpStatus.FORBIDDEN.value(),
                            e.getMessage(),
                            "/api/auth/login"
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
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> register(@RequestBody JwtRequestSchema request) {
        UserDetails userDetails;

        try {
            // save user
            userDetails = authenticationService.saveUser(request.getUsername(), request.getPassword());

            // generate token for new user
            final String token = jwtTokenUtil.generateToken(userDetails);

            // return response with status 201
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new JwtResponseSchema(token));

        } catch (DataIntegrityViolationException e) {
            // duplicate username
            // return response with status 403
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new DefaultErrorResponseSchema(
                            DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                            HttpStatus.FORBIDDEN.value(),
                            e.getMostSpecificCause().getMessage(),
                            "/api/auth/register"
                    ));
        }
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            // authenticate with username and password
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            // user is disabled
            throw new Exception("The following user is disabled!", e);
        } catch (BadCredentialsException e) {
            // invalid user credentials
            throw new Exception("Invalid username or password!", e);
        }
    }
}