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
        return new BooleanQuestionDTO(booleanQuestionEntity.getQuestionText());
    }
    // From entity to DTO with results
    public static BooleanQuestionWithResultsDTO toDtoWithResults(final BooleanQuestionEntity booleanQuestionEntity) {
        if (booleanQuestionEntity == null) {
            return null;
        }
        return new BooleanQuestionWithResultsDTO(
                booleanQuestionEntity.getQuestionText(),
                booleanQuestionEntity.getTrueVotes(),
                booleanQuestionEntity.getFalseVotes()
        );
    }
    // From DTO to entity
    public static BooleanQuestionEntity toEntity(final BooleanQuestionDTO booleanQuestionDTO) {
        if (booleanQuestionDTO == null) {
            return null;
        }
        final BooleanQuestionEntity booleanQuestionEntity = new BooleanQuestionEntity();
        booleanQuestionEntity.setQuestionText(booleanQuestionDTO.getQuestionText());
        return booleanQuestionEntity;
    }
}
