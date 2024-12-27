package org.tu.sofia.java.questionnaire.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface QuestionnaireRepository extends JpaRepository<QuestionnaireEntity, Long> {
    Optional<QuestionnaireEntity> findByVotingUrl(String votingUrl);

    @Query("SELECT q FROM QuestionnaireEntity q WHERE q.isPublic = TRUE AND q.isOpen = TRUE")
    Optional<Set<QuestionnaireEntity>> findPublic();

    @Query("SELECT q FROM QuestionnaireEntity q JOIN q.administrators a WHERE a.id = :userId")
    Optional<Set<QuestionnaireEntity>> findAdministratedQuestionnairesByUserId(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE QuestionnaireEntity q SET q.isOpen = :isOpen WHERE q.id = :questionnaireId")
    void updateQuestionnaireState(Long questionnaireId, Boolean isOpen);

    @Query("SELECT q FROM QuestionnaireEntity q JOIN q.administrators u WHERE q.resultsUrl = :resultsUrl AND u.id = :administratorId")
    Optional<QuestionnaireEntity> findByResultsUrlAndAdministratorId(String resultsUrl, Long administratorId);
}
