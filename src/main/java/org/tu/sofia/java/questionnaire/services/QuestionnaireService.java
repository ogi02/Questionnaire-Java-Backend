package org.tu.sofia.java.questionnaire.services;

import jakarta.transaction.Transactional;
import org.tu.sofia.java.questionnaire.dto.QuestionnaireDTO;
import org.tu.sofia.java.questionnaire.dto.QuestionnaireResponseDTO;
import org.tu.sofia.java.questionnaire.dto.QuestionnaireWithResultsDTO;
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
    public QuestionnaireService(QuestionnaireRepository questionnaireRepository, AuthenticationRepository authenticationRepository) {
        this.questionnaireRepository = questionnaireRepository;
        this.authenticationRepository = authenticationRepository;
    }

    public void createQuestionnaire(String username, QuestionnaireDTO questionnaireDTO) {
        // Get user by username
        UserEntity currentUser = getUserByUsername(username);

        // Map DTO to entity
        QuestionnaireEntity questionnaire = QuestionnaireMapper.toEntity(questionnaireDTO);

        // Set the current user as owner and administrator of the questionnaire
        questionnaire.setOwner(currentUser);
        questionnaire.getAdministrators().add(currentUser);

        // Save the questionnaire
        questionnaireRepository.save(questionnaire);
    }

    @Transactional
    public void deleteQuestionnaire(String username, Long questionnaireId) throws EntityNotFoundException, IllegalAccessException {
        // Get user by username
        UserEntity currentUser = getUserByUsername(username);

        // Get the questionnaire entity
        Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findById(questionnaireId);
        if (optionalQuestionnaire.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire ID not found!");
        }
        QuestionnaireEntity questionnaire = optionalQuestionnaire.get();

        // Verify that the user has the necessary rights
        Optional<QuestionnaireEntity> optionalAdministratedQuestionnaire = currentUser.getAdministratedQuestionnaires().stream().filter(administratedQuestionnaire -> Objects.equals(administratedQuestionnaire.getId(), questionnaire.getId())).findFirst();
        if (optionalAdministratedQuestionnaire.isEmpty()) {
            throw new IllegalAccessException("User is not an administrator of this entity");
        }

        // Remove administrator from the user
        currentUser.getAdministratedQuestionnaires().remove(optionalAdministratedQuestionnaire.get());
        authenticationRepository.save(currentUser);

        // Remove questionnaire
        questionnaireRepository.delete(questionnaire);
    }

    public Set<QuestionnaireDTO> findPublicQuestionnaires() {
        // Retrieve public questionnaires from DB
        Optional<Set<QuestionnaireEntity>> optionalQuestionnaireSet = questionnaireRepository.findPublic();
        if (optionalQuestionnaireSet.isEmpty()) {
            return new HashSet<>();
        }

        // Get the Optional
        Set<QuestionnaireEntity> questionnaireEntitySet = optionalQuestionnaireSet.get();

        // Map the set of entities to DTOs and return it
        return questionnaireEntitySet.stream().map(QuestionnaireMapper::toDto).collect(Collectors.toSet());
    }

    public Set<QuestionnaireWithResultsDTO> findUserAdministratedQuestionnaires(String username) {
        // Get user by username
        UserEntity currentUser = getUserByUsername(username);

        // Get all user administrated questionnaires from DB
        Optional<Set<QuestionnaireEntity>> optionalQuestionnaireSet = questionnaireRepository.findAdministratedQuestionnairesByUserId(currentUser.getId());

        // Map the set questionnaire entities to DTOs and return it
        // Return empty set if there are no questionnaires
        return optionalQuestionnaireSet.map(questionnaireEntities -> questionnaireEntities.stream().map(QuestionnaireMapper::toDtoWithResults).collect(Collectors.toSet())).orElseGet(HashSet::new);
    }

    public void updateQuestionnaireState(String username, Long questionnaireId, Boolean isOpen) throws EntityNotFoundException, IllegalAccessException {
        // Get user by username
        UserEntity currentUser = getUserByUsername(username);

        // Get questionnaire from DB
        Optional<QuestionnaireEntity> optionalQuestionnaireEntity = questionnaireRepository.findById(questionnaireId);
        if (optionalQuestionnaireEntity.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire with this ID was not found.");
        }
        QuestionnaireEntity questionnaire = optionalQuestionnaireEntity.get();

        // Validate that the current user is administrator of the questionnaire
        Optional<QuestionnaireEntity> optionalAdministratedQuestionnaire = currentUser.getAdministratedQuestionnaires().stream().filter(administratedQuestionnaire -> Objects.equals(administratedQuestionnaire.getId(), questionnaire.getId())).findFirst();
        if (optionalAdministratedQuestionnaire.isEmpty()) {
            throw new IllegalAccessException("User is not an administrator of this entity");
        }

        // Update questionnaire state
        questionnaireRepository.updateQuestionnaireState(questionnaireId, isOpen);
    }

    public void addAdministratorToQuestionnaire(String username, Long questionnaireId, Long administratorId) throws EntityNotFoundException, IllegalAccessException {
        // Get user by username
        UserEntity currentUser = getUserByUsername(username);

        // Get questionnaire from DB
        Optional<QuestionnaireEntity> optionalQuestionnaireEntity = questionnaireRepository.findById(questionnaireId);
        if (optionalQuestionnaireEntity.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire with this ID was not found.");
        }
        QuestionnaireEntity questionnaire = optionalQuestionnaireEntity.get();

        // Validate that the current user is administrator of the questionnaire
        Optional<QuestionnaireEntity> optionalAdministratedQuestionnaire = currentUser.getAdministratedQuestionnaires().stream().filter(administratedQuestionnaire -> Objects.equals(administratedQuestionnaire.getId(), questionnaire.getId())).findFirst();
        if (optionalAdministratedQuestionnaire.isEmpty()) {
            throw new IllegalAccessException("User is not an administrator of this entity");
        }

        // Get candidate administrator from DB
        Optional<UserEntity> candidateAdministrator = authenticationRepository.findById(administratorId);
        if (candidateAdministrator.isEmpty()) {
            throw new EntityNotFoundException("User with this ID was not found.");
        }
        UserEntity administrator = candidateAdministrator.get();

        // Update the questionnaire with a new administrator
        Set<UserEntity> administrators = questionnaire.getAdministrators();
        administrators.add(administrator);
        questionnaire.setAdministrators(administrators);
        questionnaireRepository.save(questionnaire);
    }

    public QuestionnaireDTO findQuestionnaireByAnswerURL(String answerURL) throws EntityNotFoundException, IllegalAccessException {
        // Get the questionnaire
        Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findByAnswerURL(answerURL);
        if (optionalQuestionnaire.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire with this voting URL was not found.");
        }
        QuestionnaireEntity questionnaire = optionalQuestionnaire.get();

        // Check if questionnaire is closed
        if (!questionnaire.getIsOpen()) {
            throw new IllegalAccessException("Questionnaire is closed.");
        }

        // Map the questionnaire entity to questionnaire DTO without results and return it
        return QuestionnaireMapper.toDto(questionnaire);
    }

    public QuestionnaireWithResultsDTO findQuestionnaireByResultsURL(String username, String resultsURL) {
        // Get user by username
        UserEntity currentUser = getUserByUsername(username);

        // Get the questionnaire entity by its results URL
        Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findByResultsUrlAndAdministratorId(resultsURL, currentUser.getId());
        if (optionalQuestionnaire.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire not found or user has no access to it.");
        }
        QuestionnaireEntity questionnaire = optionalQuestionnaire.get();

        // Map questionnaire entity to DTO with results and return it
        return QuestionnaireMapper.toDtoWithResults(questionnaire);
    }

    public void answerQuestionnaire(String answerURL, QuestionnaireResponseDTO questionnaireResponse) throws EntityNotFoundException, IllegalAccessException {
        // Get the questionnaire by its answer URL
        Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findByAnswerURL(answerURL);
        if (optionalQuestionnaire.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire with this answer URL not found.");
        }
        QuestionnaireEntity questionnaire = optionalQuestionnaire.get();

        // Check if the questionnaire is closed
        if (!questionnaire.getIsOpen()) {
            throw new IllegalAccessException("Questionnaire is closed!");
        }

        // Iterate through the answered questions
        for (Map.Entry<Long, Object> response : questionnaireResponse.getAnswers().entrySet()) {
            // Filter the questions by the ID of the response
            Optional<QuestionEntity> optionalQuestion = questionnaire.getQuestions().stream().filter(question -> Objects.equals(question.getId(), response.getKey())).findFirst();
            if (optionalQuestion.isEmpty()) {
                throw new EntityNotFoundException("Question with this ID - %d was not found.".formatted(response.getKey()));
            }
            QuestionEntity question = optionalQuestion.get();

            // Answer the question
            question.answerQuestion(response.getValue());
        }

        // Save the questionnaire
        questionnaireRepository.save(questionnaire);
    }

    private UserEntity getUserByUsername(String username) {
        Optional<UserEntity> optionalUser = authenticationRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Username not found!");
        }
        return optionalUser.get();
    }
}
