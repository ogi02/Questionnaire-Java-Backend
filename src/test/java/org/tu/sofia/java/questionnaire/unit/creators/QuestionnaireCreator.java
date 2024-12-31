package org.tu.sofia.java.questionnaire.unit.creators;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireDTO;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireWithResultsDTO;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.entities.questions.OptionQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.questions.QuestionEntity;

import java.util.HashSet;
import java.util.Set;

@Component
public final class QuestionnaireCreator {

    private static String title;
    private static String description;
    private static Boolean isOpen;
    private static Boolean isPublic;

    private QuestionnaireCreator() { }

    @Value("${unit.test.questionnaire.title}")
    public void setTitle(final String title) {
        QuestionnaireCreator.title = title;
    }

    @Value("${unit.test.questionnaire.description}")
    public void setDescription(final String description) {
        QuestionnaireCreator.description = description;
    }

    @Value("${unit.test.questionnaire.isOpen}")
    public void setIsOpen(final Boolean isOpen) {
        QuestionnaireCreator.isOpen = isOpen;
    }

    @Value("${unit.test.questionnaire.isPublic}")
    public void setIsPublic(final Boolean isPublic) {
        QuestionnaireCreator.isPublic = isPublic;
    }

    public static QuestionnaireDTO createDTO() {
        // Questionnaire DTO without results
        return new QuestionnaireDTO(
                title, description, isOpen, isPublic,
                QuestionsCreator.BooleanQuestion.createDTOS(3),
                QuestionsCreator.OpenQuestion.createDTOS(3),
                QuestionsCreator.OptionQuestion.createDTOS(3)
        );
    }

    public static QuestionnaireWithResultsDTO createDTOWithResults() {
        // Questionnaire DTO with results
        return new QuestionnaireWithResultsDTO(
                title, description, isOpen, isPublic,
                QuestionsCreator.BooleanQuestion.createDTOSWithResults(3),
                QuestionsCreator.OpenQuestion.createDTOSWithResults(3),
                QuestionsCreator.OptionQuestion.createDTOSWithResults(3)
        );
    }

    public static QuestionnaireEntity createEntity(final UserEntity owner) {
        // Questionnaire entity without results
        final QuestionnaireEntity questionnaire =
                new QuestionnaireEntity(title, description, owner, new HashSet<>(){{add(owner);}}, isOpen, isPublic);
        final Set<QuestionEntity> questions = new HashSet<>() {{
            addAll(QuestionsCreator.BooleanQuestion.createEntities(3, questionnaire));
            addAll(QuestionsCreator.OpenQuestion.createEntities(3, questionnaire));
            addAll(QuestionsCreator.OptionQuestion.createEntities(3, questionnaire));
        }};
        questionnaire.setQuestions(questions);
        return questionnaire;
    }

    public static QuestionnaireEntity createEntityWithoutIDs(final UserEntity owner) {
        // Questionnaire entity without results and without IDs
        final QuestionnaireEntity questionnaire = createEntity(owner);
        // Remove IDs
        questionnaire.getQuestions().forEach(questionEntity -> {
            if (questionEntity instanceof OptionQuestionEntity oqe) {
                oqe.getOptions().forEach(optionResponseEntity -> optionResponseEntity.setId(null));
            }
            questionEntity.setId(null);
        });
        return questionnaire;
    }

    public static QuestionnaireEntity createEntityWithResults(final UserEntity owner) {
        // Questionnaire entity without results
        final QuestionnaireEntity questionnaire =
                new QuestionnaireEntity(title, description, owner, new HashSet<>(){{add(owner);}}, isOpen, isPublic);
        final Set<QuestionEntity> questions = new HashSet<>() {{
            addAll(QuestionsCreator.BooleanQuestion.createEntitiesWithResults(3, questionnaire));
            addAll(QuestionsCreator.OpenQuestion.createEntitiesWithResults(3, questionnaire));
            addAll(QuestionsCreator.OptionQuestion.createEntitiesWithResults(3, questionnaire));
        }};
        questionnaire.setQuestions(questions);
        return questionnaire;
    }
}
