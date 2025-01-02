package org.tu.sofia.java.questionnaire.services;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.tu.sofia.java.questionnaire.config.JwtTokenUtil;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@NoArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private AuthenticationManager authenticationManager;
    private AuthenticationRepository authenticationRepository;
    private JwtTokenUtil jwtTokenUtil;
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setAuthenticationRepository(final AuthenticationRepository authenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    @Autowired
    public void setJwtTokenUtil(final JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Autowired
    public void setPasswordEncoder(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        // Find user by their username
        final Optional<UserEntity> optionalUser = authenticationRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        final UserEntity user = optionalUser.get();
        // Create and return a "UserDetails" object
        return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    public String attemptLogin(final String username, final String password) throws RuntimeException {
        // Validate input
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password must not be empty.");
        }

        try {
            // Authenticate with username and password
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            // Get user details object
            final UserDetails userDetails = loadUserByUsername(username);

            // Generate and return token
            return jwtTokenUtil.generateToken(userDetails);
        } catch (BadCredentialsException e) {
            // Invalid user credentials
            throw new RuntimeException("Invalid username or password.", e);
        } catch (Exception e) {
            // Any other exception
            throw new RuntimeException("An unexpected error occurred during login.", e);
        }
    }

    public String attemptRegister(final String username, final String password) throws RuntimeException {
        // Validate input
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password must not be empty.");
        }

        try {
            // Create the user entity
            final UserEntity userEntity = UserEntity.builder()
                    .withUsername(username)
                    .withPassword(passwordEncoder.encode(password))
                    .build();

            // Save user
            authenticationRepository.save(userEntity);

            // Create user details object
            final UserDetails userDetails =
                    new User(userEntity.getUsername(), userEntity.getPassword(), new ArrayList<>());

            // Generate and return token for new user
            return jwtTokenUtil.generateToken(userDetails);
        } catch (DataIntegrityViolationException e) {
            // If username already exists (violates "UNIQUE" constraint in the DB)
            throw new DataIntegrityViolationException("Username is already taken.", e);
        } catch (Exception e) {
            // Any other exception
            throw new RuntimeException("An unexpected error occurred during registration.", e);
        }
    }
}
