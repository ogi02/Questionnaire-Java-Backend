package org.tu.sofia.java.questionnaire.unit.repository;

import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;
import org.tu.sofia.java.questionnaire.repositories.QuestionnaireRepository;
import org.tu.sofia.java.questionnaire.unit.creators.QuestionnaireCreator;
import org.tu.sofia.java.questionnaire.unit.creators.UserCreator;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@NoArgsConstructor
public class QuestionnaireRepositoryTests {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Test
    @Transactional
    @Rollback
    public void testSaveQuestionnaire() {
        // Set up a test user (owner)
        final UserEntity user = authenticationRepository.save(UserCreator.createEntityToBeSaved());

        // Set up the questionnaire
        final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntityWithoutIDs(user);

        // Save the questionnaire
        final QuestionnaireEntity savedQuestionnaire = questionnaireRepository.save(questionnaire);

        // Assert details
        assertNotNull(savedQuestionnaire);
        assertNotNull(savedQuestionnaire.getId());
        assertEquals(questionnaire.getTitle(), savedQuestionnaire.getTitle());
        assertEquals(questionnaire.getDescription(), savedQuestionnaire.getDescription());
        assertEquals(questionnaire.getIsOpen(), savedQuestionnaire.getIsOpen());
        assertEquals(questionnaire.getIsPublic(), savedQuestionnaire.getIsPublic());

        // Assert owner
        assertEquals(user.getId(), savedQuestionnaire.getOwner().getId());
        assertEquals(1, savedQuestionnaire.getAdministrators().size());
        assertEquals(user.getId(), Objects.requireNonNull(
                savedQuestionnaire.getAdministrators().stream().findFirst().orElse(null)).getId());

        // Assert questions
        assertEquals(9, savedQuestionnaire.getQuestions().size());
    }
}
