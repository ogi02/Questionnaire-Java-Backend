package org.tu.sofia.java.questionnaire.unit.service;

import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireWithResultsDTO;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;
import org.tu.sofia.java.questionnaire.repositories.QuestionnaireRepository;
import org.tu.sofia.java.questionnaire.services.QuestionnaireService;
import org.tu.sofia.java.questionnaire.unit.creators.QuestionnaireCreator;
import org.tu.sofia.java.questionnaire.unit.creators.UserCreator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@NoArgsConstructor
public class QuestionnaireServiceTests {

    @MockitoBean
    protected AuthenticationRepository authenticationRepository;

    @MockitoBean
    protected QuestionnaireRepository questionnaireRepository;

    @Autowired
    protected QuestionnaireService questionnaireService;

    @Value("${unit.test.questionnaire.title}")
    protected String title;

    @Value("${unit.test.questionnaire.description}")
    protected String description;

    @Value("${unit.test.questionnaire.isOpen}")
    protected Boolean isOpen;

    @Value("${unit.test.questionnaire.isPublic}")
    protected Boolean isPublic;

    @Nested
    @NoArgsConstructor
    public class CreateQuestionnaire {
        @Test
        public void success() {
            // Init test user
            final UserEntity testUser = UserCreator.createEntity();

            // Mock the "findByUsername" method of the authentication repository
            when(authenticationRepository.findByUsername(any())).thenReturn(Optional.of(testUser));

            // Mock the "save" method of the questionnaire repository
            when(questionnaireRepository.save(any())).thenReturn(QuestionnaireCreator.createEntity(testUser));

            // Call the "attemptRegister" method of the service
            final QuestionnaireWithResultsDTO questionnaire =
                    questionnaireService.createQuestionnaire(testUser.getUsername(), QuestionnaireCreator.createDTO());

            // Assert that a questionnaire was returned
            assertEquals(title, questionnaire.getTitle());
            assertEquals(description, questionnaire.getDescription());
            assertEquals(isOpen, questionnaire.getIsOpen());
            assertEquals(isPublic, questionnaire.getIsPublic());
            assertEquals(3, questionnaire.getBooleanQuestionsResults().size());
            assertEquals(3, questionnaire.getOpenQuestionsResults().size());
            assertEquals(3, questionnaire.getOptionQuestionsResults().size());
        }
    }

}
