package org.tu.sofia.java.questionnaire.unit.creators;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tu.sofia.java.questionnaire.dtos.questions.*; // NOPMD
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.questions.BooleanQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.questions.OpenQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.questions.OptionQuestionEntity;

import java.util.HashSet;
import java.util.Set;

@Component
@NoArgsConstructor
public class QuestionsCreator {

    private static String booleanQuestionText;
    private static String openQuestionText;
    private static String optionQuestionText;

    @Value("${unit.test.questionnaire.boolean.question.text}")
    public void setBooleanQuestionText(final String booleanQuestionText) {
        QuestionsCreator.booleanQuestionText = booleanQuestionText;
    }

    @Value("${unit.test.questionnaire.open.question.text}")
    public void setOpenQuestionText(final String openQuestionText) {
        QuestionsCreator.openQuestionText = openQuestionText;
    }

    @Value("${unit.test.questionnaire.option.question.text}")
    public void setOptionQuestionText(final String optionQuestionText) {
        QuestionsCreator.optionQuestionText = optionQuestionText;
    }

    public static final class BooleanQuestion {

        private BooleanQuestion() {}

        public static Set<BooleanQuestionDTO> createDTOS(final int numberOfItems) {
            // Boolean Question DTOs without results
            final Set<BooleanQuestionDTO> booleanQuestionDTOS = new HashSet<>();
            for (long i = 1; i <= numberOfItems; i++) {
                booleanQuestionDTOS.add(new BooleanQuestionDTO("%s %s".formatted(booleanQuestionText, i)));
            }
            return booleanQuestionDTOS;
        }

        public static Set<BooleanQuestionWithResultsDTO> createDTOSWithResults(final int numberOfItems) {
            // Boolean Question DTOs with results
            final Set<BooleanQuestionWithResultsDTO> booleanQuestionDTOS = new HashSet<>();
            for (long i = 1; i <= numberOfItems; i++) {
                booleanQuestionDTOS.add(
                        new BooleanQuestionWithResultsDTO("%s %s".formatted(booleanQuestionText, i), 3, 3)
                );
            }
            return booleanQuestionDTOS;
        }

        public static Set<BooleanQuestionEntity> createEntities(final int numberOfItems,
                                                                final QuestionnaireEntity questionnaire) {
            // Boolean Question entities without results
            final Set<BooleanQuestionEntity> booleanQuestionEntities = new HashSet<>();
            for (long i = 1; i <= numberOfItems; i++) {
                booleanQuestionEntities.add(
                        new BooleanQuestionEntity(i, "%s %s".formatted(booleanQuestionText, i), questionnaire)
                );
            }
            return booleanQuestionEntities;
        }

        public static Set<BooleanQuestionEntity> createEntitiesWithResults(final int numberOfItems,
                                                                           final QuestionnaireEntity questionnaire) {
            // Boolean Question entities with results
            final Set<BooleanQuestionEntity> booleanQuestionEntities = new HashSet<>();
            for (long i = 1; i <= numberOfItems; i++) {
                booleanQuestionEntities.add(new BooleanQuestionEntity(
                        i, "%s %s".formatted(booleanQuestionText, i), questionnaire, 3, 3));
            }
            return booleanQuestionEntities;
        }
    }

    public static final class OpenQuestion {

        private OpenQuestion() {}

        public static Set<OpenQuestionDTO> createDTOS(final int numberOfItems) {
            // Open Question DTOs without results
            final Set<OpenQuestionDTO> openQuestionDTOS = new HashSet<>();
            for (long i = 1; i <= numberOfItems; i++) {
                openQuestionDTOS.add(new OpenQuestionDTO("%s %s".formatted(openQuestionText, i)));
            }
            return openQuestionDTOS;
        }

        public static Set<OpenQuestionWithResultsDTO> createDTOSWithResults(final int numberOfItems) {
            // Open Question DTOs with results
            final Set<OpenQuestionWithResultsDTO> openQuestionDTOS = new HashSet<>();
            for (long i = 1; i <= numberOfItems; i++) {
                openQuestionDTOS.add(new OpenQuestionWithResultsDTO(
                        "%s %s".formatted(openQuestionText, i), QuestionResponsesCreator.OpenResponse.createDTOS()));
            }
            return openQuestionDTOS;
        }

        public static Set<OpenQuestionEntity> createEntities(final int numberOfItems,
                                                             final QuestionnaireEntity questionnaire) {
            // Open Question entities without results
            final Set<OpenQuestionEntity> openQuestionEntities = new HashSet<>();
            for (long i = 1; i <= numberOfItems; i++) {
                openQuestionEntities.add(
                        new OpenQuestionEntity(i, "%s %s".formatted(openQuestionText, i), questionnaire)
                );
            }
            return openQuestionEntities;
        }

        public static Set<OpenQuestionEntity> createEntitiesWithResults(final int numberOfItems,
                                                                        final QuestionnaireEntity questionnaire) {
            // Open Question entities with results
            final Set<OpenQuestionEntity> openQuestionEntities = new HashSet<>();
            for (long i = 1; i <= numberOfItems; i++) {
                final OpenQuestionEntity question =
                        new OpenQuestionEntity(i, "%s %s".formatted(openQuestionText, i), questionnaire);
                question.setAnswers(QuestionResponsesCreator.OpenResponse.createEntities(question));
                openQuestionEntities.add(question);
            }
            return openQuestionEntities;
        }
    }

    public static final class OptionQuestion {

        private OptionQuestion() {}

        public static Set<OptionQuestionDTO> createDTOS(final int numberOfItems) {
            // Option Question DTOs without result
            final Set<OptionQuestionDTO> optionQuestionDTOS = new HashSet<>();
            for (long i = 1; i <= numberOfItems; i++) {
                optionQuestionDTOS.add(new OptionQuestionDTO(
                        "%s %s".formatted(optionQuestionText, i),
                        QuestionResponsesCreator.OptionResponse.createDTOS())
                );
            }
            return optionQuestionDTOS;
        }

        public static Set<OptionQuestionWithResultsDTO> createDTOSWithResults(final int numberOfItems) {
            // Option Question DTOs with result
            final Set<OptionQuestionWithResultsDTO> optionQuestionWithResultsDTOS = new HashSet<>();
            for (long i = 1; i <= numberOfItems; i++) {
                optionQuestionWithResultsDTOS.add(new OptionQuestionWithResultsDTO(
                        "%s %s".formatted(optionQuestionText, i),
                        QuestionResponsesCreator.OptionResponse.createDTOSWithResults())
                );
            }
            return optionQuestionWithResultsDTOS;
        }

        public static Set<OptionQuestionEntity> createEntities(final int numberOfItems,
                                                             final QuestionnaireEntity questionnaire) {
            // Open Question entities without results
            final Set<OptionQuestionEntity> optionQuestionEntities = new HashSet<>();
            for (long i = 1; i <= numberOfItems; i++) {
                final OptionQuestionEntity question =
                        new OptionQuestionEntity(i, "%s %s".formatted(openQuestionText, i), questionnaire);
                question.setOptions(QuestionResponsesCreator.OptionResponse.createEntities(question));
                optionQuestionEntities.add(question);
            }
            return optionQuestionEntities;
        }

        public static Set<OptionQuestionEntity> createEntitiesWithResults(final int numberOfItems,
                                                                        final QuestionnaireEntity questionnaire) {
            // Open Question entities with results
            final Set<OptionQuestionEntity> optionQuestionEntities = new HashSet<>();
            for (long i = 1; i <= numberOfItems; i++) {
                final OptionQuestionEntity question =
                        new OptionQuestionEntity(i, "%s %s".formatted(openQuestionText, i), questionnaire);
                question.setOptions(QuestionResponsesCreator.OptionResponse.createEntitiesWithResults(question));
                optionQuestionEntities.add(question);
            }
            return optionQuestionEntities;
        }
    }
}
