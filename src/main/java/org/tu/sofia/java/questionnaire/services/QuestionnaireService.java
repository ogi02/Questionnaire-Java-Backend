package org.tu.sofia.java.questionnaire.services;

import jakarta.transaction.Transactional;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.repositories.AuthenticationRepository;
import org.tu.sofia.java.questionnaire.repositories.QuestionnaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;

@Service
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final AuthenticationRepository authenticationRepository;

    @Autowired
    public QuestionnaireService(QuestionnaireRepository questionnaireRepository, AuthenticationRepository authenticationRepository) {
        this.questionnaireRepository = questionnaireRepository;
        this.authenticationRepository = authenticationRepository;
    }

    public void save(QuestionnaireEntity questionnaire) {
        questionnaireRepository.save(questionnaire);
    }

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
}
