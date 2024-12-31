package org.tu.sofia.java.questionnaire.unit.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireDTO;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireWithResultsDTO;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;
import org.tu.sofia.java.questionnaire.repositories.QuestionnaireRepository;
import org.tu.sofia.java.questionnaire.services.QuestionnaireService;
import org.tu.sofia.java.questionnaire.unit.creators.QuestionnaireCreator;
import org.tu.sofia.java.questionnaire.unit.creators.UserCreator;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
            // Init questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(testUser);

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(testUser))
                    .when(authenticationRepository).findByUsername(testUser.getUsername());

            // Mock the "save" method of the questionnaire repository
            doReturn(questionnaire)
                    .when(questionnaireRepository).save(any());

            // Call the "attemptRegister" method of the service
            final QuestionnaireWithResultsDTO questionnaireDTO =
                    questionnaireService.createQuestionnaire(testUser.getUsername(), QuestionnaireCreator.createDTO());

            // Assert that a questionnaire was returned
            assertEquals(title, questionnaireDTO.getTitle());
            assertEquals(description, questionnaireDTO.getDescription());
            assertEquals(isOpen, questionnaireDTO.getIsOpen());
            assertEquals(isPublic, questionnaireDTO.getIsPublic());
            assertEquals(3, questionnaireDTO.getBooleanQuestionsResults().size());
            assertEquals(3, questionnaireDTO.getOpenQuestionsResults().size());
            assertEquals(3, questionnaireDTO.getOptionQuestionsResults().size());
        }
    }

    @Nested
    @NoArgsConstructor
    public class DeleteQuestionnaire {
        @Test
        public void successAsOwner() {
            // Init test user
            final UserEntity testUser = UserCreator.createEntity();
            // Init test questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(testUser);
            // Add questionnaire to user administrated and user owned questionnaires
            testUser.getQuestionnaires().add(questionnaire);
            testUser.getAdminQuestionnaires().add(questionnaire);

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(testUser))
                    .when(authenticationRepository).findByUsername(testUser.getUsername());

            // Mock the "findById" method of the questionnaire repository
            doReturn(Optional.of(questionnaire))
                    .when(questionnaireRepository).findById(questionnaire.getId());

            // Mock the "save" method of the authentication repository
            doReturn(testUser)
                    .when(authenticationRepository).save(testUser);

            // Mock the "delete" method of the questionnaire repository
            doNothing()
                    .when(questionnaireRepository).delete(questionnaire);

            // Call the "deleteQuestionnaire" method of the service and assert that no exception is thrown
            assertDoesNotThrow(() -> questionnaireService
                    .deleteQuestionnaire(testUser.getUsername(), questionnaire.getId()));

            // Create a captor for the user entity, which is saved when the questionnaire is deleted
            final ArgumentCaptor<UserEntity> argument = ArgumentCaptor.forClass(UserEntity.class);

            // Verify every repository method was called with the specific arguments in the specific order
            verify(authenticationRepository, times(1)).findByUsername(testUser.getUsername());
            verify(questionnaireRepository, times(1)).findById(questionnaire.getId());
            // Capture the output of the "save" method of the authentication repository
            verify(authenticationRepository, times(1)).save(argument.capture());
            verify(questionnaireRepository, times(1)).delete(questionnaire);

            // Assert that the returned User Entity has the questionnaire removed from him
            assertTrue(argument.getValue().getQuestionnaires().isEmpty());
            assertTrue(argument.getValue().getAdminQuestionnaires().isEmpty());
        }

        @Test
        public void successAsAdministrator() {
            // Init test users (owner and admin)
            final UserEntity owner = UserCreator.createEntity();
            final UserEntity admin = UserCreator.createEntity();
            admin.setId(2L);
            admin.setUsername("secondTestUsername");
            // Init test questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(owner);

            // Add questionnaire to user administrated and owned questionnaires of the owner
            owner.getQuestionnaires().add(questionnaire);
            owner.getAdminQuestionnaires().add(questionnaire);

            // Add questionnaire to user administrated of the admin
            admin.getAdminQuestionnaires().add(questionnaire);

            // Mock the "findByUsername" method of the authentication repository (for the admin)
            doReturn(Optional.of(admin))
                    .when(authenticationRepository).findByUsername(admin.getUsername());

            // Mock the "findById" method of the questionnaire repository
            doReturn(Optional.of(questionnaire))
                    .when(questionnaireRepository).findById(questionnaire.getId());

            // Mock the "findByUsername" method of the authentication repository (for the owner)
            doReturn(Optional.of(owner))
                    .when(authenticationRepository).findByUsername(owner.getUsername());

            // Mock the "save" method of the authentication repository (for the owner)
            doReturn(owner)
                    .when(authenticationRepository).save(owner);

            // Mock the "save" method of the authentication repository (for the admin)
            doReturn(admin)
                    .when(authenticationRepository).save(admin);

            // Mock the "delete" method of the questionnaire repository
            doNothing()
                    .when(questionnaireRepository).delete(questionnaire);

            // Call the "deleteQuestionnaire" method of the service and assert that no exception is thrown
            assertDoesNotThrow(() -> questionnaireService
                    .deleteQuestionnaire(admin.getUsername(), questionnaire.getId()));

            // Create a captor for the user entity, which is saved when the questionnaire is deleted
            final ArgumentCaptor<UserEntity> arguments = ArgumentCaptor.forClass(UserEntity.class);

            // Verify every repository method was called with the specific arguments in the specific order
            verify(authenticationRepository, times(1)).findByUsername(admin.getUsername());
            verify(questionnaireRepository, times(1)).findById(questionnaire.getId());
            verify(authenticationRepository, times(1)).findByUsername(owner.getUsername());
            // Capture the output of the "save" method of the authentication repository
            verify(authenticationRepository, times(2)).save(arguments.capture());
            verify(questionnaireRepository, times(1)).delete(questionnaire);

            // Assert that the returned User Entities - admin and owner have the respective questionnaires removed
            // 0 should be owner, 1 should be admin
            final List<UserEntity> userEntities = arguments.getAllValues();
            assertTrue(userEntities.get(0).getQuestionnaires().isEmpty());
            assertTrue(userEntities.get(0).getAdminQuestionnaires().isEmpty());
            assertTrue(userEntities.get(1).getAdminQuestionnaires().isEmpty());
        }

        @Test
        public void failUsernameNotFound() {
            // Init test user
            final UserEntity testUser = UserCreator.createEntity();

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.empty())
                    .when(authenticationRepository).findByUsername(testUser.getUsername());

            // Assert that exception is thrown
            final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                    questionnaireService.deleteQuestionnaire(testUser.getUsername(), 1L));

            // Verify that "findByUsername" method was called
            verify(authenticationRepository, times(1)).findByUsername(testUser.getUsername());

            // Assert the exception
            assertEquals("Username not found!", exception.getMessage());
        }

        @Test
        public void failQuestionnaireNotFound() {
            // Init test user
            final UserEntity testUser = UserCreator.createEntity();
            // Init questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(testUser);

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(testUser))
                    .when(authenticationRepository).findByUsername(testUser.getUsername());

            // Mock the "findById" method of the questionnaire repository
            doReturn(Optional.empty())
                    .when(questionnaireRepository).findById(questionnaire.getId());

            // Assert that exception is thrown
            final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                    questionnaireService.deleteQuestionnaire(testUser.getUsername(), questionnaire.getId()));

            // Verify that all methods were called in the correct order
            verify(authenticationRepository, times(1)).findByUsername(testUser.getUsername());
            verify(questionnaireRepository, times(1)).findById(questionnaire.getId());

            // Assert the exception
            assertEquals("Questionnaire ID not found!", exception.getMessage());
        }

        @Test
        public void failUserIsNotAdministratorOfQuestionnaire() {
            // Init test user
            final UserEntity testUser = UserCreator.createEntity();
            // Init questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(testUser);

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(testUser))
                    .when(authenticationRepository).findByUsername(testUser.getUsername());

            // Mock the "findById" method of the questionnaire repository
            doReturn(Optional.of(questionnaire))
                    .when(questionnaireRepository).findById(questionnaire.getId());

            // Assert that exception is thrown
            final IllegalAccessException exception = assertThrows(IllegalAccessException.class, () ->
                    questionnaireService.deleteQuestionnaire(testUser.getUsername(), questionnaire.getId()));

            // Verify that all methods were called in the correct order
            verify(authenticationRepository, times(1)).findByUsername(testUser.getUsername());
            verify(questionnaireRepository, times(1)).findById(questionnaire.getId());

            // Assert the exception
            assertEquals("User is not an administrator of this questionnaire.", exception.getMessage());
        }

        @Test
        public void failOwnerIdNotFound() {
            // Init test users (owner and admin)
            final UserEntity owner = UserCreator.createEntity();
            final UserEntity admin = UserCreator.createEntity();
            admin.setId(2L);
            admin.setUsername("secondTestUsername");
            // Init test questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(owner);

            // Add questionnaire to user administrated and owned questionnaires of the owner
            owner.getQuestionnaires().add(questionnaire);
            owner.getAdminQuestionnaires().add(questionnaire);

            // Add questionnaire to user administrated of the admin
            admin.getAdminQuestionnaires().add(questionnaire);

            // Mock the "findByUsername" method of the authentication repository (for the admin)
            doReturn(Optional.of(admin))
                    .when(authenticationRepository).findByUsername(admin.getUsername());

            // Mock the "findById" method of the questionnaire repository
            doReturn(Optional.of(questionnaire))
                    .when(questionnaireRepository).findById(questionnaire.getId());

            // Mock the "findByUsername" method of the authentication repository (for the owner)
            doReturn(Optional.empty())
                    .when(authenticationRepository).findByUsername(owner.getUsername());

            // Assert that exception is thrown
            final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                    questionnaireService.deleteQuestionnaire(admin.getUsername(), questionnaire.getId()));

            // Verify every repository method was called with the specific arguments in the specific order
            verify(authenticationRepository, times(1)).findByUsername(admin.getUsername());
            verify(questionnaireRepository, times(1)).findById(questionnaire.getId());
            verify(authenticationRepository, times(1)).findByUsername(owner.getUsername());

            // Assert the exception
            assertEquals("User ID of the owner of the questionnaire was not found.", exception.getMessage());
        }
    }

    @Nested
    @NoArgsConstructor
    public class FindPublicQuestionnaires {
        @Test
        public void successPresent() {
            // Init test user
            final UserEntity testUser = UserCreator.createEntity();
            // Init questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(testUser);

            // Mock the "findPublic" method of the questionnaire repository
            doReturn(Optional.of(Set.of(questionnaire)))
                    .when(questionnaireRepository).findPublic();

            // Call the "findPublicQuestionnaires" method of the service
            final Set<QuestionnaireDTO> questionnaireDTOSet =
                    questionnaireService.findPublicQuestionnaires();

            // Get the questionnaire DTO
            final QuestionnaireDTO questionnaireDTO = questionnaireDTOSet.stream().findFirst().orElse(null);

            // Verify "findPublic" was called
            verify(questionnaireRepository, times(1)).findPublic();

            // Assert that the questionnaire is present
            assertNotNull(questionnaireDTO);

            // Assert the questionnaire
            assertEquals(title, questionnaireDTO.getTitle());
            assertEquals(description, questionnaireDTO.getDescription());
            assertEquals(isOpen, questionnaireDTO.getIsOpen());
            assertEquals(isPublic, questionnaireDTO.getIsPublic());
            assertEquals(3, questionnaireDTO.getBooleanQuestionDTOSet().size());
            assertEquals(3, questionnaireDTO.getOpenQuestionDTOSet().size());
            assertEquals(3, questionnaireDTO.getOptionQuestionDTOSet().size());
        }

        @Test
        public void successEmpty() {
            // Init test user
            final UserEntity testUser = UserCreator.createEntity();
            // Init questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(testUser);
            questionnaire.setIsPublic(false);

            // Mock the "findPublic" method of the questionnaire repository
            doReturn(Optional.empty())
                    .when(questionnaireRepository).findPublic();

            // Call the "findPublicQuestionnaires" method of the service
            final Set<QuestionnaireDTO> questionnaireDTOSet =
                    questionnaireService.findPublicQuestionnaires();

            // Verify "findPublic" was called
            verify(questionnaireRepository, times(1)).findPublic();

            // Assert that no questionnaire was returned
            assertTrue(questionnaireDTOSet.isEmpty());
        }
    }

    @Nested
    @NoArgsConstructor
    public class FindUserAdministratedQuestionnaires {
        @Test
        public void successPresent() {
            // Init test user
            final UserEntity testUser = UserCreator.createEntity();
            // Init questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(testUser);
            // Add questionnaire to user administrated
            testUser.getAdminQuestionnaires().add(questionnaire);

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(testUser))
                    .when(authenticationRepository).findByUsername(testUser.getUsername());

            // Mock the "findAdministratedQuestionnairesByUserId" method of the questionnaire repository
            doReturn(Optional.of(Set.of(questionnaire)))
                    .when(questionnaireRepository).findAdministratedQuestionnairesByUserId(testUser.getId());

            // Call the "findUserAdministratedQuestionnaires" method of the service
            final Set<QuestionnaireWithResultsDTO> questionnaireDTOSet =
                    questionnaireService.findUserAdministratedQuestionnaires(testUser.getUsername());

            // Get the questionnaire DTO
            final QuestionnaireWithResultsDTO questionnaireDTO =
                    questionnaireDTOSet.stream().findFirst().orElse(null);

            // Verify the methods were called in the correct order
            verify(authenticationRepository, times(1)).findByUsername(testUser.getUsername());
            verify(questionnaireRepository, times(1)).findAdministratedQuestionnairesByUserId(testUser.getId());

            // Assert that the questionnaire is present
            assertNotNull(questionnaireDTO);

            // Assert the questionnaire
            assertEquals(title, questionnaireDTO.getTitle());
            assertEquals(description, questionnaireDTO.getDescription());
            assertEquals(isOpen, questionnaireDTO.getIsOpen());
            assertEquals(isPublic, questionnaireDTO.getIsPublic());
            assertEquals(3, questionnaireDTO.getBooleanQuestionsResults().size());
            assertEquals(3, questionnaireDTO.getOpenQuestionsResults().size());
            assertEquals(3, questionnaireDTO.getOptionQuestionsResults().size());
        }

        @Test
        public void successEmpty() {
            // Init test user
            final UserEntity testUser = UserCreator.createEntity();
            // Init questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(testUser);
            // Remove owner from questionnaire
            questionnaire.setOwner(null);

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(testUser))
                    .when(authenticationRepository).findByUsername(testUser.getUsername());

            // Mock the "findAdministratedQuestionnairesByUserId" method of the questionnaire repository
            doReturn(Optional.empty())
                    .when(questionnaireRepository).findAdministratedQuestionnairesByUserId(testUser.getId());

            // Call the "attemptRegister" method of the service
            final Set<QuestionnaireWithResultsDTO> questionnaireDTOSet =
                    questionnaireService.findUserAdministratedQuestionnaires(testUser.getUsername());

            // Get the questionnaire DTO
            final QuestionnaireWithResultsDTO questionnaireDTO =
                    questionnaireDTOSet.stream().findFirst().orElse(null);

            // Verify the methods were called in the correct order
            verify(authenticationRepository, times(1)).findByUsername(testUser.getUsername());
            verify(questionnaireRepository, times(1)).findAdministratedQuestionnairesByUserId(testUser.getId());

            // Assert there is no questionnaire
            assertNull(questionnaireDTO);
        }

        @Test
        public void failUsernameNotFound() {
            // Init test user
            final UserEntity testUser = UserCreator.createEntity();

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.empty())
                    .when(authenticationRepository).findByUsername(testUser.getUsername());

            // Assert exception is thrown
            final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                    questionnaireService.findUserAdministratedQuestionnaires(testUser.getUsername()));

            // Verify the methods were called in the correct order
            verify(authenticationRepository, times(1)).findByUsername(testUser.getUsername());

            // Assert the exception
            assertEquals("Username not found!", exception.getMessage());
        }
    }

    @Nested
    @NoArgsConstructor
    public class UpdateQuestionnaireState {
        @Test
        public void success() {
            // Init test users
            final UserEntity owner = UserCreator.createEntity();
            // Init test questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(owner);
            // Add questionnaire to user administrated and user owned questionnaires
            owner.getQuestionnaires().add(questionnaire);
            owner.getAdminQuestionnaires().add(questionnaire);

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(owner))
                    .when(authenticationRepository).findByUsername(owner.getUsername());

            // Mock the "findById" method of the questionnaire repository
            doReturn(Optional.of(questionnaire))
                    .when(questionnaireRepository).findById(questionnaire.getId());

            // Mock the "save" method of the questionnaire repository
            doReturn(questionnaire)
                    .when(questionnaireRepository).save(questionnaire);

            // Call the "updateQuestionnaireState" method of the service and assert that no exception is thrown
            assertDoesNotThrow(() -> questionnaireService
                    .updateQuestionnaireState(owner.getUsername(), questionnaire.getId(), false));

            // Create a captor for the questionnaire
            final ArgumentCaptor<QuestionnaireEntity> questionnaireArgument =
                    ArgumentCaptor.forClass(QuestionnaireEntity.class);

            // Verify every repository method was called with the specific arguments in the specific order
            verify(authenticationRepository, times(1)).findByUsername(owner.getUsername());
            verify(questionnaireRepository, times(1)).findById(questionnaire.getId());
            // Capture the output of the "save" method of the questionnaire repositories
            verify(questionnaireRepository, times(1)).save(questionnaireArgument.capture());

            // Assert that the returned Questionnaire Entity is closed
            assertFalse(questionnaireArgument.getValue().getIsOpen());
        }

        @Test
        public void failUsernameNotFound() {
            // Init test users
            final UserEntity owner = UserCreator.createEntity();

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.empty())
                    .when(authenticationRepository).findByUsername(owner.getUsername());

            // Call the "updateQuestionnaireState" method of the service and assert that exception is thrown
            final EntityNotFoundException exception =
                    assertThrows(EntityNotFoundException.class, () -> questionnaireService
                            .updateQuestionnaireState(owner.getUsername(), 0L, false));

            // Verify every repository method was called with the specific arguments in the specific order
            verify(authenticationRepository, times(1)).findByUsername(owner.getUsername());

            // Assert the exception
            assertEquals("Username not found!", exception.getMessage());
        }

        @Test
        public void failQuestionnaireIdNotFound() {
            // Init test users
            final UserEntity owner = UserCreator.createEntity();
            // Init test questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(owner);

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(owner))
                    .when(authenticationRepository).findByUsername(owner.getUsername());

            // Mock the "findById" method of the questionnaire repository
            doReturn(Optional.empty())
                    .when(questionnaireRepository).findById(questionnaire.getId());

            // Call the "updateQuestionnaireState" method of the service and assert that exception is thrown
            final EntityNotFoundException exception =
                    assertThrows(EntityNotFoundException.class, () -> questionnaireService
                            .updateQuestionnaireState(owner.getUsername(), questionnaire.getId(), false));

            // Verify every repository method was called with the specific arguments in the specific order
            verify(authenticationRepository, times(1)).findByUsername(owner.getUsername());
            verify(questionnaireRepository, times(1)).findById(questionnaire.getId());

            // Assert the exception
            assertEquals("Questionnaire with this ID was not found.", exception.getMessage());
        }

        @Test
        public void failUserNotAdministrator() {
            // Init test users
            final UserEntity owner = UserCreator.createEntity();
            // Init test questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(owner);

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(owner))
                    .when(authenticationRepository).findByUsername(owner.getUsername());

            // Mock the "findById" method of the questionnaire repository
            doReturn(Optional.of(questionnaire))
                    .when(questionnaireRepository).findById(questionnaire.getId());

            // Call the "updateQuestionnaireState" method of the service and assert that exception is thrown
            final IllegalAccessException exception =
                    assertThrows(IllegalAccessException.class, () -> questionnaireService
                            .updateQuestionnaireState(owner.getUsername(), questionnaire.getId(), false));

            // Verify every repository method was called with the specific arguments in the specific order
            verify(authenticationRepository, times(1)).findByUsername(owner.getUsername());
            verify(questionnaireRepository, times(1)).findById(questionnaire.getId());

            // Assert the exception
            assertEquals("User is not an administrator of this entity", exception.getMessage());
        }
    }

    @Nested
    @NoArgsConstructor
    public class AddAdministratorToQuestionnaire {
        @Test
        public void success() {
            // Init test users
            final UserEntity owner = UserCreator.createEntity();
            final UserEntity admin = UserCreator.createEntity();
            admin.setId(2L);
            // Init test questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(owner);
            // Add questionnaire to user administrated and user owned questionnaires
            owner.getQuestionnaires().add(questionnaire);
            owner.getAdminQuestionnaires().add(questionnaire);

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(owner))
                    .when(authenticationRepository).findByUsername(owner.getUsername());

            // Mock the "findById" method of the questionnaire repository
            doReturn(Optional.of(questionnaire))
                    .when(questionnaireRepository).findById(questionnaire.getId());

            // Mock the "findById" method of the authentication repository (for the candidate admin)
            doReturn(Optional.of(admin))
                    .when(authenticationRepository).findById(admin.getId());

            // Mock the "save" method of the questionnaire repository
            doReturn(questionnaire)
                    .when(questionnaireRepository).save(questionnaire);

            // Mock the "save" method of the authentication repository
            doReturn(admin)
                    .when(authenticationRepository).save(admin);

            // Call the "addAdministratorToQuestionnaire" method of the service and assert that no exception is thrown
            assertDoesNotThrow(() -> questionnaireService
                    .addAdministratorToQuestionnaire(owner.getUsername(), questionnaire.getId(), admin.getId()));

            // Create captors for the admin and the questionnaire
            final ArgumentCaptor<UserEntity> userArgument = ArgumentCaptor.forClass(UserEntity.class);
            final ArgumentCaptor<QuestionnaireEntity> questionnaireArgument =
                    ArgumentCaptor.forClass(QuestionnaireEntity.class);

            // Verify every repository method was called with the specific arguments in the specific order
            verify(authenticationRepository, times(1)).findByUsername(owner.getUsername());
            verify(questionnaireRepository, times(1)).findById(questionnaire.getId());
            verify(authenticationRepository, times(1)).findById(admin.getId());
            // Capture the output of the "save" method of both repositories
            verify(questionnaireRepository, times(1)).save(questionnaireArgument.capture());
            verify(authenticationRepository, times(1)).save(userArgument.capture());

            // Assert that the returned Questionnaire Entity has the admin added
            assertEquals(2, questionnaireArgument.getValue().getAdministrators().size());

            // Assert that the returned User Entity has the questionnaire add to the user administrated set
            assertTrue(userArgument.getValue().getQuestionnaires().isEmpty());
            assertFalse(userArgument.getValue().getAdminQuestionnaires().isEmpty());
        }

        @Test
        public void failUsernameNotFound() {
            // Init test users
            final UserEntity owner = UserCreator.createEntity();

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.empty())
                    .when(authenticationRepository).findByUsername(owner.getUsername());

            // Call the "addAdministratorToQuestionnaire" method of the service and assert that exception is thrown
            final EntityNotFoundException exception =
                    assertThrows(EntityNotFoundException.class, () -> questionnaireService
                            .addAdministratorToQuestionnaire(owner.getUsername(), 0L, 0L));

            // Verify every repository method was called with the specific arguments in the specific order
            verify(authenticationRepository, times(1)).findByUsername(owner.getUsername());

            // Assert the exception
            assertEquals("Username not found!", exception.getMessage());
        }

        @Test
        public void failQuestionnaireIdNotFound() {
            // Init test users
            final UserEntity owner = UserCreator.createEntity();
            // Init test questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(owner);

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(owner))
                    .when(authenticationRepository).findByUsername(owner.getUsername());

            // Mock the "findById" method of the questionnaire repository
            doReturn(Optional.empty())
                    .when(questionnaireRepository).findById(questionnaire.getId());

            // Call the "addAdministratorToQuestionnaire" method of the service and assert that exception is thrown
            final EntityNotFoundException exception =
                    assertThrows(EntityNotFoundException.class, () -> questionnaireService
                            .addAdministratorToQuestionnaire(owner.getUsername(), questionnaire.getId(), any()));

            // Verify every repository method was called with the specific arguments in the specific order
            verify(authenticationRepository, times(1)).findByUsername(owner.getUsername());
            verify(questionnaireRepository, times(1)).findById(questionnaire.getId());

            // Assert the exception
            assertEquals("Questionnaire with this ID was not found.", exception.getMessage());
        }

        @Test
        public void failUserIsNotAdministrator() {
            // Init test users
            final UserEntity owner = UserCreator.createEntity();
            // Init test questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(owner);

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(owner))
                    .when(authenticationRepository).findByUsername(owner.getUsername());

            // Mock the "findById" method of the questionnaire repository
            doReturn(Optional.of(questionnaire))
                    .when(questionnaireRepository).findById(questionnaire.getId());

            // Call the "addAdministratorToQuestionnaire" method of the service and assert that exception is thrown
            final IllegalAccessException exception =
                    assertThrows(IllegalAccessException.class, () -> questionnaireService
                            .addAdministratorToQuestionnaire(owner.getUsername(), questionnaire.getId(), any()));

            // Verify every repository method was called with the specific arguments in the specific order
            verify(authenticationRepository, times(1)).findByUsername(owner.getUsername());
            verify(questionnaireRepository, times(1)).findById(questionnaire.getId());

            // Assert the exception
            assertEquals("User is not an administrator of this entity", exception.getMessage());
        }

        @Test
        public void failCandidateAdministratorIdNotFound() {
            // Init test users
            final UserEntity owner = UserCreator.createEntity();
            final UserEntity admin = UserCreator.createEntity();
            admin.setId(2L);
            // Init test questionnaire
            final QuestionnaireEntity questionnaire = QuestionnaireCreator.createEntity(owner);
            // Add questionnaire to user administrated and user owned questionnaires
            owner.getQuestionnaires().add(questionnaire);
            owner.getAdminQuestionnaires().add(questionnaire);

            // Mock the "findByUsername" method of the authentication repository
            doReturn(Optional.of(owner))
                    .when(authenticationRepository).findByUsername(owner.getUsername());

            // Mock the "findById" method of the questionnaire repository
            doReturn(Optional.of(questionnaire))
                    .when(questionnaireRepository).findById(questionnaire.getId());

            // Mock the "findById" method of the authentication repository (for the candidate admin)
            doReturn(Optional.empty())
                    .when(authenticationRepository).findById(admin.getId());

            // Call the "addAdministratorToQuestionnaire" method of the service and assert that exception is thrown
            final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                    questionnaireService.addAdministratorToQuestionnaire(
                            owner.getUsername(), questionnaire.getId(), admin.getId()));

            // Verify every repository method was called with the specific arguments in the specific order
            verify(authenticationRepository, times(1)).findByUsername(owner.getUsername());
            verify(questionnaireRepository, times(1)).findById(questionnaire.getId());
            verify(authenticationRepository, times(1)).findById(admin.getId());

            // Assert the exception
            assertEquals("User with this ID was not found.", exception.getMessage());
        }
    }
}
