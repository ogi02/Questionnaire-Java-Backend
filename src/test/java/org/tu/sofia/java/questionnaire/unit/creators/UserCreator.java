package org.tu.sofia.java.questionnaire.unit.creators;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tu.sofia.java.questionnaire.entities.UserEntity;

import java.util.HashSet;

@Component
@NoArgsConstructor
public class UserCreator {

    private static String testUsername;
    private static String testPassword;

    @Value("${unit.test.username}")
    public void setTestUsername(final String username) {
        testUsername = username;
    }

    @Value("${unit.test.password}")
    public void setTestPassword(final String password) {
        testPassword = password;
    }

    public static UserEntity createEntity() {
        return new UserEntity(1L, testUsername, testPassword, new HashSet<>(), new HashSet<>());
    }

    public static UserEntity createEntityToBeSaved() {
        return new UserEntity(testUsername, testPassword);
    }
}
