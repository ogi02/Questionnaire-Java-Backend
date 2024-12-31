package org.tu.sofia.java.questionnaire.unit.creators;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tu.sofia.java.questionnaire.dtos.responses.OpenResponseWithResultsDTO;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseDTO;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseWithResultsDTO;
import org.tu.sofia.java.questionnaire.entities.questions.OpenQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.questions.OptionQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.responses.OpenResponseEntity;
import org.tu.sofia.java.questionnaire.entities.responses.OptionResponseEntity;

import java.util.HashSet;
import java.util.Set;

@Component
@NoArgsConstructor
public class QuestionResponsesCreator {

    private static String openQuestionResponseText;
    private static String optionQuestionResponseText;

    @Value("${unit.test.questionnaire.open.question.response.text}")
    public void setOpenQuestionResponseText(final String openQuestionResponseText) {
        QuestionResponsesCreator.openQuestionResponseText = openQuestionResponseText;
    }

    @Value("${unit.test.questionnaire.option.question.response.text}")
    public void setOptionQuestionResponseText(final String optionQuestionResponseText) {
        QuestionResponsesCreator.optionQuestionResponseText = optionQuestionResponseText;
    }

    public static final class OpenResponse {

        private OpenResponse() { }

        public static Set<OpenResponseWithResultsDTO> createDTOS() {
            // Open Response DTOs with results
            final Set<OpenResponseWithResultsDTO> openResponseDTOS = new HashSet<>();
            for (long i = 1; i <= 3; i++) {
                openResponseDTOS.add(
                        new OpenResponseWithResultsDTO("%s %s".formatted(openQuestionResponseText, i))
                );
            }
            return openResponseDTOS;
        }

        public static Set<OpenResponseEntity> createEntities(final OpenQuestionEntity question) {
            // Open Response entities with results
            final Set<OpenResponseEntity> openResponseEntities = new HashSet<>();
            for (long i = 1; i <= 3; i++) {
                openResponseEntities.add(
                        new OpenResponseEntity(i, question, "%s %s".formatted(openQuestionResponseText, i))
                );
            }
            return openResponseEntities;
        }
    }

    public static final class OptionResponse {

        private OptionResponse() { }

        public static Set<OptionResponseDTO> createDTOS() {
            // Option Response DTOs without results
            final Set<OptionResponseDTO> optionResponseDTOS = new HashSet<>();
            for (long i = 1; i <= 3; i++) {
                optionResponseDTOS.add(
                        new OptionResponseDTO("%s %s".formatted(optionQuestionResponseText, i))
                );
            }
            return optionResponseDTOS;
        }

        public static Set<OptionResponseWithResultsDTO> createDTOSWithResults() {
            // Option Response DTOs with results
            final Set<OptionResponseWithResultsDTO> optionResponsesDTOS = new HashSet<>();
            for (long i = 1; i <= 3; i++) {
                optionResponsesDTOS.add(
                        new OptionResponseWithResultsDTO("%s %s".formatted(optionQuestionResponseText, i), 3)
                );
            }
            return optionResponsesDTOS;
        }

        public static Set<OptionResponseEntity> createEntities(final OptionQuestionEntity question) {
            // Option Response entities without results
            final Set<OptionResponseEntity> optionResponseEntities = new HashSet<>();
            for (long i = 1; i <= 3; i++) {
                optionResponseEntities.add(
                        new OptionResponseEntity(i, question, "%s %s".formatted(optionQuestionResponseText, i), 0)
                );
            }
            return optionResponseEntities;
        }

        public static Set<OptionResponseEntity> createEntitiesWithResults(final OptionQuestionEntity question) {
            // Option Response entities with results
            final Set<OptionResponseEntity> optionResponseEntities = new HashSet<>();
            for (long i = 1; i <= 3; i++) {
                optionResponseEntities.add(
                        new OptionResponseEntity(i, question, "%s %s".formatted(optionQuestionResponseText, i), 3)
                );
            }
            return optionResponseEntities;
        }
    }
}
