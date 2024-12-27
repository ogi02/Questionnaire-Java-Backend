package org.tu.sofia.java.questionnaire.mappers.questions;

import org.tu.sofia.java.questionnaire.dtos.questions.BooleanQuestionDTO;
import org.tu.sofia.java.questionnaire.dtos.questions.BooleanQuestionWithResultsDTO;
import org.tu.sofia.java.questionnaire.entities.questions.BooleanQuestionEntity;

public class BooleanQuestionMapper {
    // From entity to DTO
    public static BooleanQuestionDTO toDto(BooleanQuestionEntity booleanQuestionEntity) {
        if (booleanQuestionEntity == null) {
            return null;
        }
        return new BooleanQuestionDTO(booleanQuestionEntity.getQuestionText());
    }
    // From entity to DTO with results
    public static BooleanQuestionWithResultsDTO toDtoWithResults(BooleanQuestionEntity booleanQuestionEntity) {
        if (booleanQuestionEntity == null) {
            return null;
        }
        return new BooleanQuestionWithResultsDTO(booleanQuestionEntity.getQuestionText(), booleanQuestionEntity.getTrueVotes(), booleanQuestionEntity.getFalseVotes());
    }
    // From DTO to entity
    public static BooleanQuestionEntity toEntity(BooleanQuestionDTO booleanQuestionDTO) {
        if (booleanQuestionDTO == null) {
            return null;
        }
        BooleanQuestionEntity booleanQuestionEntity = new BooleanQuestionEntity();
        booleanQuestionEntity.setQuestionText(booleanQuestionDTO.getQuestionText());
        return booleanQuestionEntity;
    }
}
