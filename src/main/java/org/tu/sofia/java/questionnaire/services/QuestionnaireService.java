package org.tu.sofia.java.questionnaire.services;

import jakarta.transaction.Transactional;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireDTO;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireResponseDTO;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireWithResultsDTO;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.entities.questions.QuestionEntity;
import org.tu.sofia.java.questionnaire.mappers.QuestionnaireMapper;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;
import org.tu.sofia.java.questionnaire.repositories.QuestionnaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final AuthenticationRepository authenticationRepository;

    @Autowired
    public QuestionnaireService(final QuestionnaireRepository questionnaireRepository,
                                final AuthenticationRepository authenticationRepository
    ) {
        this.questionnaireRepository = questionnaireRepository;
        this.authenticationRepository = authenticationRepository;
    }

    public QuestionnaireWithResultsDTO createQuestionnaire(
            final String username, final QuestionnaireDTO questionnaireDTO
    ) {
        // Get user by username
        final UserEntity currentUser = getUserByUsername(username);

        // Map DTO to entity
        final QuestionnaireEntity questionnaire = QuestionnaireMapper.toEntity(questionnaireDTO);

        // Set the current user as owner and administrator of the questionnaire
        questionnaire.setOwner(currentUser);
        questionnaire.getAdministrators().add(currentUser);

        // Save the questionnaire
        return QuestionnaireMapper.toDtoWithResults(questionnaireRepository.save(questionnaire));
    }

    @Transactional
    public void deleteQuestionnaire(
            final String username, final Long questionnaireId
    ) throws EntityNotFoundException, IllegalAccessException {
        // Get user by username
        final UserEntity currentUser = getUserByUsername(username);

        // Get the questionnaire entity
        final Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findById(questionnaireId);
        if (optionalQuestionnaire.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire ID not found!");
        }
        final QuestionnaireEntity questionnaire = optionalQuestionnaire.get();

        // Verify that the user has the necessary rights
        final Optional<QuestionnaireEntity> optionalAdminQuestionnaire = currentUser.getAdminQuestionnaires()
                .stream()
                .filter(adminQuestionnaire ->
                        Objects.equals(adminQuestionnaire.getId(), questionnaire.getId()))
                .findFirst();
        if (optionalAdminQuestionnaire.isEmpty()) {
            throw new IllegalAccessException("User is not an administrator of this questionnaire.");
        }

        // Remove administrator from the user
        currentUser.getAdminQuestionnaires().remove(optionalAdminQuestionnaire.get());

        // Check if current user is owner
        if (optionalAdminQuestionnaire.get().getOwner().getId().equals(currentUser.getId())) {
            // Remove questionnaire from current user
            currentUser.getQuestionnaires().remove(optionalAdminQuestionnaire.get());
        } else {
            // Get owner
            final Optional<UserEntity> optionalOwner =
                    authenticationRepository.findByUsername(questionnaire.getOwner().getUsername());
            if (optionalOwner.isEmpty()) {
                throw new EntityNotFoundException("User ID of the owner of the questionnaire was not found.");
            }
            final UserEntity owner = optionalOwner.get();
            // Remove questionnaire from owner
            owner.getQuestionnaires().remove(optionalAdminQuestionnaire.get());
            owner.getAdminQuestionnaires().remove(optionalAdminQuestionnaire.get());
            // Save owner entity
            authenticationRepository.save(owner);
        }

        // Save current user
        authenticationRepository.save(currentUser);

        // Remove questionnaire
        questionnaireRepository.delete(questionnaire);
    }

    public Set<QuestionnaireDTO> findPublicQuestionnaires() {
        // Retrieve public questionnaires from DB
        final Optional<Set<QuestionnaireEntity>> optionalQuestionnaireSet = questionnaireRepository.findPublic();
        if (optionalQuestionnaireSet.isEmpty()) {
            return new HashSet<>();
        }

        // Get the Optional
        final Set<QuestionnaireEntity> questionnaireEntitySet = optionalQuestionnaireSet.get();

        // Map the set of entities to DTOs and return it
        return questionnaireEntitySet.stream().map(QuestionnaireMapper::toDto).collect(Collectors.toSet());
    }

    public Set<QuestionnaireWithResultsDTO> findUserAdministratedQuestionnaires(final String username) {
        // Get user by username
        final UserEntity currentUser = getUserByUsername(username);

        // Get all user administrated questionnaires from DB
        final Optional<Set<QuestionnaireEntity>> optionalQuestionnaireSet =
                questionnaireRepository.findAdministratedQuestionnairesByUserId(currentUser.getId());

        // Map the set questionnaire entities to DTOs and return it
        // Return empty set if there are no questionnaires
        return optionalQuestionnaireSet.map(questionnaireEntities -> questionnaireEntities
                .stream()
                .map(QuestionnaireMapper::toDtoWithResults).collect(Collectors.toSet())).orElseGet(HashSet::new);
    }

    public void updateQuestionnaireState(final String username, final Long questionnaireId, final Boolean isOpen)
            throws EntityNotFoundException, IllegalAccessException {
        // Get user by username
        final UserEntity currentUser = getUserByUsername(username);

        // Get questionnaire from DB
        final Optional<QuestionnaireEntity> optionalQuestionnaireEntity =
                questionnaireRepository.findById(questionnaireId);
        if (optionalQuestionnaireEntity.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire with this ID was not found.");
        }
        final QuestionnaireEntity questionnaire = optionalQuestionnaireEntity.get();

        // Validate that the current user is administrator of the questionnaire
        final Optional<QuestionnaireEntity> optionalAdminQuestionnaire = currentUser.getAdminQuestionnaires()
                .stream()
                .filter(adminQuestionnaire ->
                        Objects.equals(adminQuestionnaire.getId(), questionnaire.getId()))
                .findFirst();
        if (optionalAdminQuestionnaire.isEmpty()) {
            throw new IllegalAccessException("User is not an administrator of this entity");
        }

        // Update questionnaire state
        questionnaire.setIsOpen(isOpen);
        questionnaireRepository.save(questionnaire);
    }

    public void addAdministratorToQuestionnaire(
            final String username, final Long questionnaireId, final Long administratorId
    ) throws EntityNotFoundException, IllegalAccessException {
        // Get user by username
        final UserEntity currentUser = getUserByUsername(username);

        // Get questionnaire from DB
        final Optional<QuestionnaireEntity> optionalQuestionnaireEntity =
                questionnaireRepository.findById(questionnaireId);
        if (optionalQuestionnaireEntity.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire with this ID was not found.");
        }
        final QuestionnaireEntity questionnaire = optionalQuestionnaireEntity.get();

        // Validate that the current user is administrator of the questionnaire
        final Optional<QuestionnaireEntity> optionalAdminQuestionnaire = currentUser.getAdminQuestionnaires()
                .stream()
                .filter(adminQuestionnaire ->
                        Objects.equals(adminQuestionnaire.getId(), questionnaire.getId()))
                .findFirst();
        if (optionalAdminQuestionnaire.isEmpty()) {
            throw new IllegalAccessException("User is not an administrator of this entity");
        }

        // Get candidate administrator from DB
        final Optional<UserEntity> candidateAdministrator = authenticationRepository.findById(administratorId);
        if (candidateAdministrator.isEmpty()) {
            throw new EntityNotFoundException("User with this ID was not found.");
        }
        final UserEntity administrator = candidateAdministrator.get();

        // Update the questionnaire with a new administrator
        questionnaire.getAdministrators().add(administrator);
        questionnaireRepository.save(questionnaire);

        // Update the administrator
        administrator.getAdminQuestionnaires().add(questionnaire);
        authenticationRepository.save(administrator);
    }

    public QuestionnaireDTO findQuestionnaireByAnswerURL(final String answerURL)
            throws EntityNotFoundException, IllegalAccessException {
        // Get the questionnaire
        final Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findByAnswerURL(answerURL);
        if (optionalQuestionnaire.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire with this answer URL was not found.");
        }
        final QuestionnaireEntity questionnaire = optionalQuestionnaire.get();

        // Check if questionnaire is closed
        if (!questionnaire.getIsOpen()) {
            throw new IllegalAccessException("Questionnaire is closed.");
        }

        // Map the questionnaire entity to questionnaire DTO without results and return it
        return QuestionnaireMapper.toDto(questionnaire);
    }

    public QuestionnaireWithResultsDTO findQuestionnaireByResultsURL(final String username, final String resultsURL) {
        // Get user by username
        final UserEntity currentUser = getUserByUsername(username);

        // Get the questionnaire entity by its results URL
        final Optional<QuestionnaireEntity> optionalQuestionnaire =
                questionnaireRepository.findByResultsURLAndAdministratorId(resultsURL, currentUser.getId());
        if (optionalQuestionnaire.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire not found or user has no access to it.");
        }
        final QuestionnaireEntity questionnaire = optionalQuestionnaire.get();

        // Map questionnaire entity to DTO with results and return it
        return QuestionnaireMapper.toDtoWithResults(questionnaire);
    }

    public void answerQuestionnaire(
            final String answerURL, final QuestionnaireResponseDTO questionnaireResponse
    ) throws EntityNotFoundException, IllegalAccessException {
        // Get the questionnaire by its answer URL
        final Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findByAnswerURL(answerURL);
        if (optionalQuestionnaire.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire with this answer URL was not found.");
        }
        final QuestionnaireEntity questionnaire = optionalQuestionnaire.get();

        // Check if the questionnaire is closed
        if (!questionnaire.getIsOpen()) {
            throw new IllegalAccessException("Questionnaire is closed.");
        }

        // Iterate through the answered questions
        for (final Map.Entry<Long, Object> response : questionnaireResponse.getAnswers().entrySet()) {
            // Filter the questions by the ID of the response
            final Optional<QuestionEntity> optionalQuestion = questionnaire.getQuestions()
                    .stream().filter(question -> Objects.equals(question.getId(), response.getKey())).findFirst();
            if (optionalQuestion.isEmpty()) {
                throw new EntityNotFoundException(
                        "Question with this ID - %d was not found.".formatted(response.getKey())
                );
            }
            final QuestionEntity question = optionalQuestion.get();

            // Answer the question
            question.answerQuestion(response.getValue());
        }

        // Save the questionnaire
        questionnaireRepository.save(questionnaire);
    }

    private UserEntity getUserByUsername(final String username) {
        final Optional<UserEntity> optionalUser = authenticationRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Username not found!");
        }
        return optionalUser.get();
    }
}
