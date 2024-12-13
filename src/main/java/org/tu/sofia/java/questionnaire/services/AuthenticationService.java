package org.tu.sofia.java.questionnaire.services;

import jakarta.persistence.EntityNotFoundException;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
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
public class AuthenticationService implements UserDetailsService {

    private final AuthenticationRepository authenticationRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(AuthenticationRepository authenticationRepository, PasswordEncoder passwordEncoder) {
        this.authenticationRepository = authenticationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = authenticationRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        UserEntity user = optionalUser.get();

        return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    public UserEntity loadUserModelByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = authenticationRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        return optionalUser.get();
    }

    public UserEntity getById(Long id) throws EntityNotFoundException {
        Optional<UserEntity> optionalUser = authenticationRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException(String.valueOf(id));
        }

        return optionalUser.get();
    }

    public UserDetails saveUser(String username, String password) {
        UserEntity userEntity = new UserEntity(username, passwordEncoder.encode(password));

        authenticationRepository.save(userEntity);

        return new User(userEntity.getUsername(), userEntity.getPassword(), new ArrayList<>());
    }
}
