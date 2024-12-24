package org.tu.sofia.java.questionnaire.services;

import jakarta.transaction.Transactional;
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
