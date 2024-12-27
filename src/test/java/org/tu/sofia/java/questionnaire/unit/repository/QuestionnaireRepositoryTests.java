package org.tu.sofia.java.questionnaire.unit.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.entities.questions.BooleanQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.questions.OpenQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.questions.OptionQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.questions.QuestionEntity;
import org.tu.sofia.java.questionnaire.entities.responses.OptionResponseEntity;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;
import org.tu.sofia.java.questionnaire.repositories.QuestionnaireRepository;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class QuestionnaireRepositoryTests {
    private AuthenticationRepository authenticationRepository;
    private QuestionnaireRepository questionnaireRepository;

    private UserEntity testSessionUser;

    @Autowired
    public void setAuthenticationRepository(AuthenticationRepository authenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    @Autowired
    public void setQuestionnaireRepository(QuestionnaireRepository questionnaireRepository) {
        this.questionnaireRepository = questionnaireRepository;
    }

    @BeforeEach
    public void createUser() {
        // Set up details for test user
        String username = "testUsername";
        String password = "testPassword";
        // Create a user with the test data
        UserEntity user = new UserEntity(username, password);
        // Save the user and assign it to the testSessionUser object
        testSessionUser = authenticationRepository.save(user);
    }

    private QuestionnaireEntity setupQuestionnaire() {
        // Define basic questionnaire details
        String title = "Test Questionnaire title";
        String description = "Test Questionnaire description";
        Boolean isOpen = true;
        Boolean isPublic = true;

        // Define the owner of the questionnaire
        UserEntity owner = testSessionUser;
        Set<UserEntity> administrators = Set.of(testSessionUser);

        // Define the questions of the questionnaire
        Set<QuestionEntity> questions = setupQuestions();

        // Create and return a questionnaire
        return new QuestionnaireEntity(title, description, owner, administrators, questions, isOpen, isPublic);
    }

    private Set<QuestionEntity> setupQuestions() {
        // Define the boolean questions of the questionnaire
        BooleanQuestionEntity booleanQuestion1 = new BooleanQuestionEntity("Test Boolean Question 1");
        BooleanQuestionEntity booleanQuestion2 = new BooleanQuestionEntity("Test Boolean Question 2");
        BooleanQuestionEntity booleanQuestion3 = new BooleanQuestionEntity("Test Boolean Question 3");
        Set<BooleanQuestionEntity> booleanQuestions = Set.of(booleanQuestion1, booleanQuestion2, booleanQuestion3);

        // Define the open questions of the questionnaire
        OpenQuestionEntity openQuestion1 = new OpenQuestionEntity("Test Open Question 1");
        OpenQuestionEntity openQuestion2 = new OpenQuestionEntity("Test Open Question 2");
        OpenQuestionEntity openQuestion3 = new OpenQuestionEntity("Test Open Question 3");
        Set<OpenQuestionEntity> openQuestions = Set.of(openQuestion1, openQuestion2, openQuestion3);

        // Define the option questions of the questionnaire
        OptionResponseEntity option1ForOptionQuestion1 = new OptionResponseEntity("Option 1 for Option Question 1");
        OptionResponseEntity option2ForOptionQuestion1 = new OptionResponseEntity("Option 2 for Option Question 1");
        OptionResponseEntity option3ForOptionQuestion1 = new OptionResponseEntity("Option 3 for Option Question 1");
        OptionQuestionEntity optionQuestion1 = new OptionQuestionEntity("Test Option Question 1", Set.of(option1ForOptionQuestion1, option2ForOptionQuestion1, option3ForOptionQuestion1));
        OptionResponseEntity option1ForOptionQuestion2 = new OptionResponseEntity("Option 1 for Option Question 2");
        OptionResponseEntity option2ForOptionQuestion2 = new OptionResponseEntity("Option 2 for Option Question 2");
        OptionResponseEntity option3ForOptionQuestion2 = new OptionResponseEntity("Option 3 for Option Question 2");
        OptionQuestionEntity optionQuestion2 = new OptionQuestionEntity("Test Option Question 2", Set.of(option1ForOptionQuestion2, option2ForOptionQuestion2, option3ForOptionQuestion2));
        OptionResponseEntity option1ForOptionQuestion3 = new OptionResponseEntity("Option 1 for Option Question 3");
        OptionResponseEntity option2ForOptionQuestion3 = new OptionResponseEntity("Option 2 for Option Question 3");
        OptionResponseEntity option3ForOptionQuestion3 = new OptionResponseEntity("Option 3 for Option Question 3");
        OptionQuestionEntity optionQuestion3 = new OptionQuestionEntity("Test Option Question 3", Set.of(option1ForOptionQuestion3, option2ForOptionQuestion3, option3ForOptionQuestion3));
        Set<OptionQuestionEntity> optionQuestions = Set.of(optionQuestion1, optionQuestion2, optionQuestion3);

        // Define the questions of the questionnaire
        return new HashSet<>() {{
            addAll(booleanQuestions);
            addAll(openQuestions);
            addAll(optionQuestions);
        }};
    }

    @Test
    @Transactional
    @Rollback
    public void testSaveQuestionnaire() {
        // Set up the questionnaire
        QuestionnaireEntity questionnaire = setupQuestionnaire();

        // Save the questionnaire
        QuestionnaireEntity savedQuestionnaire = questionnaireRepository.save(questionnaire);

        // Assert details
        assertNotNull(savedQuestionnaire);
        assertNotNull(savedQuestionnaire.getId());
        assertEquals(questionnaire.getTitle(), savedQuestionnaire.getTitle());
        assertEquals(questionnaire.getDescription(), savedQuestionnaire.getDescription());
        assertEquals(questionnaire.getIsOpen(), savedQuestionnaire.getIsOpen());
        assertEquals(questionnaire.getIsPublic(), savedQuestionnaire.getIsPublic());

        // Assert owner
        assertEquals(testSessionUser.getId(), savedQuestionnaire.getOwner().getId());
        assertEquals(1, savedQuestionnaire.getAdministrators().size());
        assertEquals(testSessionUser.getId(), Objects.requireNonNull(savedQuestionnaire.getAdministrators().stream().findFirst().orElse(null)).getId());

        // Assert questions
        assertEquals(9, savedQuestionnaire.getQuestions().size());
    }
}
