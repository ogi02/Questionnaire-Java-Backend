package org.tu.sofia.java.questionnaire.services;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.tu.sofia.java.questionnaire.dto.QuestionnaireCreationDTO;
import org.tu.sofia.java.questionnaire.dto.QuestionnaireDTO;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.mappers.QuestionnaireMapper;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;
import org.tu.sofia.java.questionnaire.repositories.QuestionnaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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

    public void createQuestionnaire(String username, QuestionnaireCreationDTO questionnaireCreationDTO) {
        // Get user by username
        UserEntity currentUser = getUserByUsername(username);

        // Map DTO to entity
        QuestionnaireEntity questionnaire = QuestionnaireMapper.toEntity(questionnaireCreationDTO);

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

    public Set<QuestionnaireDTO> findUserAdministratedQuestionnaires(String username) {
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


    // ------------------------------------------------------

    public QuestionnaireEntity findByQuestionnaireId(Long questionnaireId) throws EntityNotFoundException {
        Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findById(questionnaireId);
        if (optionalQuestionnaire.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire ID not found!");
        }

        return optionalQuestionnaire.get();
    }

    public QuestionnaireEntity findByQuestionnaireIdAndAdministratorId(Long questionnaireId, Long administratorId) throws EntityNotFoundException {
        Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findByQuestionnaireIdAndAdministratorId(questionnaireId, administratorId);
        if (optionalQuestionnaire.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire ID or this administrator ID is not found!");
        }

        return optionalQuestionnaire.get();
    }

    public QuestionnaireEntity findByVotingUrl(String votingUrl) throws EntityNotFoundException {
        Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findByVotingUrl(votingUrl);
        if (optionalQuestionnaire.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire voting url not found!");
        }

        return optionalQuestionnaire.get();
    }

    public QuestionnaireEntity findByResultsUrlAndAdministratorId(String resultsUrl, Long administratorId) throws EntityNotFoundException {
        Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findByResultsUrlAndAdministratorId(resultsUrl, administratorId);
        if (optionalQuestionnaire.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire results url not found!");
        }

        return optionalQuestionnaire.get();
    }

    public Set<QuestionnaireEntity> findPublic() {
        Optional<Set<QuestionnaireEntity>> optionalQuestionnaireSet = questionnaireRepository.findPublic();
        return optionalQuestionnaireSet.orElse(null);
    }

    public Set<QuestionnaireEntity> findByOwner(UserEntity owner) {
        Optional<Set<QuestionnaireEntity>> optionalQuestionnaireSet = questionnaireRepository.findByOwnerId(owner.getId());
        return optionalQuestionnaireSet.orElse(null);
    }

    @Transactional
    public void addAdministratorToQuestionnaire(Long questionnaireId, Long userId) {
        Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findById(questionnaireId);
        Optional<UserEntity> optionalUser = authenticationRepository.findById(userId);

        if (optionalQuestionnaire.isPresent() && optionalUser.isPresent()) {
            QuestionnaireEntity questionnaire = optionalQuestionnaire.get();
            UserEntity user = optionalUser.get();

            questionnaire.getAdministrators().add(user);
            user.getAdministratedQuestionnaires().add(questionnaire);

            authenticationRepository.save(user);
        }
    }

    private UserEntity getUserByUsername(String username) {
        Optional<UserEntity> optionalUser = authenticationRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("Username not found!");
        }
        return optionalUser.get();
    }
}
