package org.tu.sofia.java.questionnaire.repositories;

import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface QuestionnaireRepository extends JpaRepository<QuestionnaireEntity, Long> {
    Optional<QuestionnaireEntity> findByVotingUrl(String votingUrl);

    Optional<Set<QuestionnaireEntity>> findByOwnerId(Long ownerId);

    @Query("SELECT q FROM QuestionnaireEntity q WHERE q.isPublic = TRUE AND q.isOpen = TRUE")
    Optional<Set<QuestionnaireEntity>> findPublic();

    @Query("SELECT q FROM QuestionnaireEntity q WHERE q.id = ?1 AND q.owner.id = ?2")
    Optional<QuestionnaireEntity> findByQuestionnaireIdAndOwnerId(Long questionnaireId, Long ownerId);

    @Query("SELECT q FROM QuestionnaireEntity q WHERE q.resultsUrl = ?1 AND q.owner.id = ?2")
    Optional<QuestionnaireEntity> findByResultsUrlAndOwnerId(String resultsUrl, Long ownerId);
}
