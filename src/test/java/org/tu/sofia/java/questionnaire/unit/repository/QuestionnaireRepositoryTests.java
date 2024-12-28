package org.tu.sofia.java.questionnaire.unit.repository;

import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class QuestionnaireRepositoryTests {
    private AuthenticationRepository authenticationRepository;
    private QuestionnaireRepository questionnaireRepository;

    private UserEntity testSessionUser;

    @Autowired
    public void setAuthenticationRepository(final AuthenticationRepository authenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    @Autowired
    public void setQuestionnaireRepository(final QuestionnaireRepository questionnaireRepository) {
        this.questionnaireRepository = questionnaireRepository;
    }

    @BeforeEach
    public void createUser() {
        // Set up details for test user
        final String username = "testUsername";
        final String password = "testPassword";
        // Create a user with the test data
        final UserEntity user = new UserEntity(username, password);
        // Save the user and assign it to the testSessionUser object
        testSessionUser = authenticationRepository.save(user);
    }

    private QuestionnaireEntity setupQuestionnaire() {
        // Define basic questionnaire details
        final String title = "Test Questionnaire title";
        final String description = "Test Questionnaire description";
        final Boolean isOpen = true;
        final Boolean isPublic = true;

        // Define the owner of the questionnaire
        final UserEntity owner = testSessionUser;
        final Set<UserEntity> administrators = Set.of(testSessionUser);

        // Define the questions of the questionnaire
        final Set<QuestionEntity> questions = setupQuestions();

        // Create and return a questionnaire
        return new QuestionnaireEntity(title, description, owner, administrators, questions, isOpen, isPublic);
    }

    private Set<QuestionEntity> setupQuestions() {
        // Define the boolean questions of the questionnaire
        final BooleanQuestionEntity booleanQuestion1 = new BooleanQuestionEntity("Test Boolean Question 1");
        final BooleanQuestionEntity booleanQuestion2 = new BooleanQuestionEntity("Test Boolean Question 2");
        final BooleanQuestionEntity booleanQuestion3 = new BooleanQuestionEntity("Test Boolean Question 3");
        final Set<BooleanQuestionEntity> booleanQuestions =
                Set.of(booleanQuestion1, booleanQuestion2, booleanQuestion3);

        // Define the open questions of the questionnaire
        final OpenQuestionEntity openQuestion1 = new OpenQuestionEntity("Test Open Question 1");
        final OpenQuestionEntity openQuestion2 = new OpenQuestionEntity("Test Open Question 2");
        final OpenQuestionEntity openQuestion3 = new OpenQuestionEntity("Test Open Question 3");
        final Set<OpenQuestionEntity> openQuestions = Set.of(openQuestion1, openQuestion2, openQuestion3);

        // Define the option questions of the questionnaire
        final OptionResponseEntity option1Question1 = new OptionResponseEntity("Option 1 for Option Question 1");
        final OptionResponseEntity option2Question1 = new OptionResponseEntity("Option 2 for Option Question 1");
        final OptionResponseEntity option3Question1 = new OptionResponseEntity("Option 3 for Option Question 1");
        final OptionQuestionEntity optionQuestion1 = new OptionQuestionEntity("Test Option Question 1",
                Set.of(option1Question1, option2Question1, option3Question1));
        final OptionResponseEntity option1Question2 = new OptionResponseEntity("Option 1 for Option Question 2");
        final OptionResponseEntity option2Question2 = new OptionResponseEntity("Option 2 for Option Question 2");
        final OptionResponseEntity option3Question2 = new OptionResponseEntity("Option 3 for Option Question 2");
        final OptionQuestionEntity optionQuestion2 = new OptionQuestionEntity("Test Option Question 2",
                Set.of(option1Question2, option2Question2, option3Question2));
        final OptionResponseEntity option1Question3 = new OptionResponseEntity("Option 1 for Option Question 3");
        final OptionResponseEntity option2Question3 = new OptionResponseEntity("Option 2 for Option Question 3");
        final OptionResponseEntity option3Question3 = new OptionResponseEntity("Option 3 for Option Question 3");
        final OptionQuestionEntity optionQuestion3 = new OptionQuestionEntity("Test Option Question 3",
                Set.of(option1Question3, option2Question3, option3Question3));
        final Set<OptionQuestionEntity> optionQuestions = Set.of(optionQuestion1, optionQuestion2, optionQuestion3);

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
        final QuestionnaireEntity questionnaire = setupQuestionnaire();

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
        assertEquals(testSessionUser.getId(), savedQuestionnaire.getOwner().getId());
        assertEquals(1, savedQuestionnaire.getAdministrators().size());
        assertEquals(testSessionUser.getId(), Objects.requireNonNull(
                savedQuestionnaire.getAdministrators().stream().findFirst().orElse(null)).getId());

        // Assert questions
        assertEquals(9, savedQuestionnaire.getQuestions().size());
    }
}
