package org.tu.sofia.java.questionnaire.services;

import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.repositories.QuestionnaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;

@Service
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;

    @Autowired
    public QuestionnaireService(QuestionnaireRepository questionnaireRepository) {
        this.questionnaireRepository = questionnaireRepository;
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

    public QuestionnaireEntity findByQuestionnaireIdAndOwnerId(Long questionnaireId, Long ownerId) throws EntityNotFoundException {
        Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findByQuestionnaireIdAndOwnerId(questionnaireId, ownerId);
        if (optionalQuestionnaire.isEmpty()) {
            throw new EntityNotFoundException("Questionnaire ID for this user is not found!");
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

    public QuestionnaireEntity findByResultsUrlAndOwnerId(String resultsUrl, Long ownerId) throws EntityNotFoundException {
        Optional<QuestionnaireEntity> optionalQuestionnaire = questionnaireRepository.findByResultsUrlAndOwnerId(resultsUrl, ownerId);
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
}
