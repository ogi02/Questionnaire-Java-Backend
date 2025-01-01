package org.tu.sofia.java.questionnaire.unit.repository;

import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;
import org.tu.sofia.java.questionnaire.repositories.QuestionnaireRepository;
import org.tu.sofia.java.questionnaire.unit.utilities.QuestionnaireCreator;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@NoArgsConstructor
@ComponentScan("org.tu.sofia.java.questionnaire.unit.utilities")
public class QuestionnaireRepositoryTests {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Value("${unit.test.username}")
    private String testUsername;

    @Value("${unit.test.password}")
    private String testPassword;

    @Test
    public void testSaveQuestionnaire() {
        // Set up a test user (owner)
        final UserEntity testUser = authenticationRepository.save(
                new UserEntity.Builder().withUsername(testUsername).withPassword(testPassword).build());

        // Set up the questionnaire
        final QuestionnaireEntity questionnaire = QuestionnaireCreator.createDefaultEntity(testUser, false);

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
        assertEquals(testUser.getId(), savedQuestionnaire.getOwner().getId());
        assertEquals(1, savedQuestionnaire.getAdministrators().size());
        assertEquals(testUser.getId(), Objects.requireNonNull(
                savedQuestionnaire.getAdministrators().stream().findFirst().orElse(null)).getId());

        // Assert questions
        assertEquals(9, savedQuestionnaire.getQuestions().size());
    }

    @Nested
    @NoArgsConstructor
    public class FindByAnswerURL {
        @Test
        public void found() {
            // Set up a test user (owner)
            final UserEntity testUser = authenticationRepository.save(
                    new UserEntity.Builder().withUsername(testUsername).withPassword(testPassword).build());

            // Set up and save the questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createDefaultEntity(testUser, false);
            questionnaireRepository.save(questionnaire);

            // Find the questionnaire by answer URL
            final Optional<QuestionnaireEntity> optionalQuestionnaire =
                    questionnaireRepository.findByAnswerURL(questionnaire.getAnswerURL());

            // Assert that the questionnaire was found
            assertTrue(optionalQuestionnaire.isPresent());
            final QuestionnaireEntity foundQuestionnaire = optionalQuestionnaire.get();

            // Assert details
            assertNotNull(foundQuestionnaire.getId());
            assertEquals(questionnaire.getTitle(), foundQuestionnaire.getTitle());
            assertEquals(questionnaire.getDescription(), foundQuestionnaire.getDescription());
            assertEquals(questionnaire.getIsOpen(), foundQuestionnaire.getIsOpen());
            assertEquals(questionnaire.getIsPublic(), foundQuestionnaire.getIsPublic());

            // Assert owner
            assertEquals(testUser.getId(), foundQuestionnaire.getOwner().getId());
            assertEquals(1, foundQuestionnaire.getAdministrators().size());
            assertEquals(testUser.getId(), Objects.requireNonNull(
                    foundQuestionnaire.getAdministrators().stream().findFirst().orElse(null)).getId());

            // Assert questions
            assertEquals(9, foundQuestionnaire.getQuestions().size());
        }

        @Test
        public void notFound() {
            final UserEntity testUser = authenticationRepository.save(
                    new UserEntity.Builder().withUsername(testUsername).withPassword(testPassword).build());

            // Set up and save the questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createDefaultEntity(testUser, false);
            questionnaireRepository.save(questionnaire);

            // Find the questionnaire by answer URL
            final Optional<QuestionnaireEntity> optionalQuestionnaire =
                    questionnaireRepository.findByAnswerURL("invalidAnswerURL");

            // Assert that the questionnaire was not found
            assertTrue(optionalQuestionnaire.isEmpty());
        }
    }

    @Nested
    @NoArgsConstructor
    public class FindPublic {
        @Test
        public void found() {
            final UserEntity testUser = authenticationRepository.save(
                    new UserEntity.Builder().withUsername(testUsername).withPassword(testPassword).build());

            // Set up and save the questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createDefaultEntity(testUser, false);
            questionnaireRepository.save(questionnaire);

            // Find public questionnaire
            final Optional<Set<QuestionnaireEntity>> optionalQuestionnaireEntitySet =
                    questionnaireRepository.findPublic();

            // Assert that a set is returned
            assertTrue(optionalQuestionnaireEntitySet.isPresent());
            final Set<QuestionnaireEntity> foundQuestionnaires = optionalQuestionnaireEntitySet.get();

            // Assert that only one questionnaire was found
            assertEquals(1, foundQuestionnaires.size());
            final QuestionnaireEntity foundQuestionnaire = foundQuestionnaires.stream().findFirst().orElse(null);

            // Assert details
            assertNotNull(foundQuestionnaire);
            assertNotNull(foundQuestionnaire.getId());
            assertEquals(questionnaire.getTitle(), foundQuestionnaire.getTitle());
            assertEquals(questionnaire.getDescription(), foundQuestionnaire.getDescription());
            assertEquals(questionnaire.getIsOpen(), foundQuestionnaire.getIsOpen());
            assertEquals(questionnaire.getIsPublic(), foundQuestionnaire.getIsPublic());

            // Assert owner
            assertEquals(testUser.getId(), foundQuestionnaire.getOwner().getId());
            assertEquals(1, foundQuestionnaire.getAdministrators().size());
            assertEquals(testUser.getId(), Objects.requireNonNull(
                    foundQuestionnaire.getAdministrators().stream().findFirst().orElse(null)).getId());

            // Assert questions
            assertEquals(9, foundQuestionnaire.getQuestions().size());
        }

        @Test
        public void notFound() {
            final UserEntity testUser = authenticationRepository.save(
                    new UserEntity.Builder().withUsername(testUsername).withPassword(testPassword).build());

            // Set up and save the questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createDefaultEntity(testUser, false);
            questionnaire.setIsPublic(false);
            questionnaireRepository.save(questionnaire);

            // Find public questionnaire
            final Optional<Set<QuestionnaireEntity>> optionalQuestionnaireEntitySet =
                    questionnaireRepository.findPublic();

            // Assert that a set is not returned
            assertTrue(optionalQuestionnaireEntitySet.isPresent());
            final Set<QuestionnaireEntity> foundQuestionnaires = optionalQuestionnaireEntitySet.get();

            // Assert that no questionnaires were found
            assertTrue(foundQuestionnaires.isEmpty());
        }
    }

    @Nested
    @NoArgsConstructor
    public class FindAdministratedQuestionnairesByUserId {
        @Test
        public void found() {
            final UserEntity testUser = authenticationRepository.save(
                    new UserEntity.Builder().withUsername(testUsername).withPassword(testPassword).build());

            // Set up and save the questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createDefaultEntity(testUser, false);
            questionnaireRepository.save(questionnaire);

            // Find administrated questionnaire by the user
            final Optional<Set<QuestionnaireEntity>> optionalQuestionnaireEntitySet =
                    questionnaireRepository.findAdministratedQuestionnairesByUserId(testUser.getId());

            // Assert that a set is returned
            assertTrue(optionalQuestionnaireEntitySet.isPresent());
            final Set<QuestionnaireEntity> foundQuestionnaires = optionalQuestionnaireEntitySet.get();

            // Assert that only one questionnaire was found
            assertEquals(1, foundQuestionnaires.size());
            final QuestionnaireEntity foundQuestionnaire = foundQuestionnaires.stream().findFirst().orElse(null);

            // Assert details
            assertNotNull(foundQuestionnaire);
            assertNotNull(foundQuestionnaire.getId());
            assertEquals(questionnaire.getTitle(), foundQuestionnaire.getTitle());
            assertEquals(questionnaire.getDescription(), foundQuestionnaire.getDescription());
            assertEquals(questionnaire.getIsOpen(), foundQuestionnaire.getIsOpen());
            assertEquals(questionnaire.getIsPublic(), foundQuestionnaire.getIsPublic());

            // Assert administrator
            assertEquals(1, foundQuestionnaire.getAdministrators().size());
            assertEquals(testUser.getId(), Objects.requireNonNull(
                    foundQuestionnaire.getAdministrators().stream().findFirst().orElse(null)).getId());

            // Assert questions
            assertEquals(9, foundQuestionnaire.getQuestions().size());
        }

        @Test
        public void notFound() {
            final UserEntity testUser = authenticationRepository.save(
                    new UserEntity.Builder().withUsername(testUsername).withPassword(testPassword).build());

            // Set up and save the questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createDefaultEntity(testUser, false);
            questionnaireRepository.save(questionnaire);

            // Find administrated questionnaire by the user
            final Optional<Set<QuestionnaireEntity>> optionalQuestionnaireEntitySet =
                    questionnaireRepository.findAdministratedQuestionnairesByUserId(testUser.getId() + 1L);

            // Assert that a set is not returned
            assertTrue(optionalQuestionnaireEntitySet.isPresent());
            final Set<QuestionnaireEntity> foundQuestionnaires = optionalQuestionnaireEntitySet.get();

            // Assert that no questionnaires were found
            assertTrue(foundQuestionnaires.isEmpty());
        }
    }

    @Nested
    @NoArgsConstructor
    public class FindByResultsURLAndAdministratorIdFound {
        @Test
        public void found() {
            final UserEntity testUser = authenticationRepository.save(
                    new UserEntity.Builder().withUsername(testUsername).withPassword(testPassword).build());

            // Set up and save the questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createDefaultEntity(testUser, false);
            questionnaireRepository.save(questionnaire);

            // Find the questionnaire by results URL and administrator ID
            final Optional<QuestionnaireEntity> optionalQuestionnaireEntity = questionnaireRepository
                    .findByResultsURLAndAdministratorId(questionnaire.getResultsURL(), testUser.getId());

            // Assert that a questionnaire is returned
            assertTrue(optionalQuestionnaireEntity.isPresent());
            final QuestionnaireEntity foundQuestionnaire = optionalQuestionnaireEntity.get();

            // Assert details
            assertNotNull(foundQuestionnaire);
            assertNotNull(foundQuestionnaire.getId());
            assertEquals(questionnaire.getTitle(), foundQuestionnaire.getTitle());
            assertEquals(questionnaire.getDescription(), foundQuestionnaire.getDescription());
            assertEquals(questionnaire.getIsOpen(), foundQuestionnaire.getIsOpen());
            assertEquals(questionnaire.getIsPublic(), foundQuestionnaire.getIsPublic());

            // Assert administrator
            assertEquals(1, foundQuestionnaire.getAdministrators().size());
            assertEquals(testUser.getId(), Objects.requireNonNull(
                    foundQuestionnaire.getAdministrators().stream().findFirst().orElse(null)).getId());

            // Assert questions
            assertEquals(9, foundQuestionnaire.getQuestions().size());
        }

        @Test
        public void resultsURLNotFound() {
            final UserEntity testUser = authenticationRepository.save(
                    new UserEntity.Builder().withUsername(testUsername).withPassword(testPassword).build());

            // Set up and save the questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createDefaultEntity(testUser, false);
            questionnaireRepository.save(questionnaire);

            // Find the questionnaire by results URL and administrator ID
            final Optional<QuestionnaireEntity> optionalQuestionnaireEntity = questionnaireRepository
                    .findByResultsURLAndAdministratorId("invalidResultsURL", testUser.getId());

            // Assert that no questionnaires were found
            assertTrue(optionalQuestionnaireEntity.isEmpty());
        }

        @Test
        public void administratorIdNotFound() {
            final UserEntity testUser = authenticationRepository.save(
                    new UserEntity.Builder().withUsername(testUsername).withPassword(testPassword).build());

            // Set up and save the questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createDefaultEntity(testUser, false);
            questionnaireRepository.save(questionnaire);

            // Find the questionnaire by results URL and administrator ID
            final Optional<QuestionnaireEntity> optionalQuestionnaireEntity = questionnaireRepository
                    .findByResultsURLAndAdministratorId(questionnaire.getResultsURL(), testUser.getId() + 1L);

            // Assert that no questionnaires were found
            assertTrue(optionalQuestionnaireEntity.isEmpty());
        }
    }
}
