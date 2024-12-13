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

    @Query("SELECT q FROM QuestionnaireEntity q JOIN q.administrators u WHERE q.id = :questionnaireId AND u.id = :administratorId")
    Optional<QuestionnaireEntity> findByQuestionnaireIdAndAdministratorId(Long questionnaireId, Long administratorId);

    @Query("SELECT q FROM QuestionnaireEntity q JOIN q.administrators u WHERE q.resultsUrl = :resultsUrl AND u.id = :administratorId")
    Optional<QuestionnaireEntity> findByResultsUrlAndAdministratorId(String resultsUrl, Long administratorId);
}
