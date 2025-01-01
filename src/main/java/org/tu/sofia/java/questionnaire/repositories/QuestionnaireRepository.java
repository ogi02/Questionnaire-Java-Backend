package org.tu.sofia.java.questionnaire.repositories;

import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface QuestionnaireRepository extends JpaRepository<QuestionnaireEntity, Long> {
    Optional<QuestionnaireEntity> findByAnswerURL(String answerURL);

    @Query("SELECT q FROM QuestionnaireEntity q WHERE q.isPublic = TRUE AND q.isOpen = TRUE")
    Optional<Set<QuestionnaireEntity>> findPublic();

    @Query("SELECT q FROM QuestionnaireEntity q JOIN q.administrators a WHERE a.id = :userId")
    Optional<Set<QuestionnaireEntity>> findAdministratedQuestionnairesByUserId(Long userId);

    @Query("SELECT q FROM QuestionnaireEntity q JOIN q.administrators u " +
            "WHERE q.resultsURL = :resultsURL AND u.id = :administratorId")
    Optional<QuestionnaireEntity> findByResultsURLAndAdministratorId(String resultsURL, Long administratorId);
}
