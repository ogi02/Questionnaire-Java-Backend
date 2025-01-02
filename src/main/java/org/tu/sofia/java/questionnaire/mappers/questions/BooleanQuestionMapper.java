package org.tu.sofia.java.questionnaire.mappers.questions;

import lombok.experimental.UtilityClass;
import org.tu.sofia.java.questionnaire.dtos.questions.BooleanQuestionDTO;
import org.tu.sofia.java.questionnaire.dtos.questions.BooleanQuestionWithResultsDTO;
import org.tu.sofia.java.questionnaire.entities.questions.BooleanQuestionEntity;

@UtilityClass
public class BooleanQuestionMapper {
    // From entity to DTO
    public static BooleanQuestionDTO toDto(final BooleanQuestionEntity booleanQuestionEntity) {
        if (booleanQuestionEntity == null) {
            return null;
        }
        return BooleanQuestionDTO.builder().withQuestionText(booleanQuestionEntity.getQuestionText()).build();
    }
    // From entity to DTO with results
    public static BooleanQuestionWithResultsDTO toDtoWithResults(final BooleanQuestionEntity booleanQuestionEntity) {
        if (booleanQuestionEntity == null) {
            return null;
        }
        return BooleanQuestionWithResultsDTO.builder()
                .withQuestionText(booleanQuestionEntity.getQuestionText())
                .withTrueVotes(booleanQuestionEntity.getTrueVotes())
                .withFalseVotes(booleanQuestionEntity.getFalseVotes())
                .build();
    }
    // From DTO to entity
    public static BooleanQuestionEntity toEntity(final BooleanQuestionDTO booleanQuestionDTO) {
        if (booleanQuestionDTO == null) {
            return null;
        }
        return BooleanQuestionEntity.builder().withQuestionText(booleanQuestionDTO.getQuestionText()).build();
    }
}
