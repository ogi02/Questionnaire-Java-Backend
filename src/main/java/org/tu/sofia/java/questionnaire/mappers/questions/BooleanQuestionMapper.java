package org.tu.sofia.java.questionnaire.mappers.questions;

import org.tu.sofia.java.questionnaire.dto.questions.BooleanQuestionCreationDTO;
import org.tu.sofia.java.questionnaire.dto.questions.BooleanQuestionDTO;
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
    public static BooleanQuestionDTO toDtoWithResults(BooleanQuestionEntity booleanQuestionEntity) {
        if (booleanQuestionEntity == null) {
            return null;
        }
        return new BooleanQuestionDTO(booleanQuestionEntity.getQuestionText(), booleanQuestionEntity.getTrueVotes(), booleanQuestionEntity.getFalseVotes());
    }
    // From DTO to entity
    public static BooleanQuestionEntity toEntity(BooleanQuestionCreationDTO booleanQuestionCreationDTO) {
        if (booleanQuestionCreationDTO == null) {
            return null;
        }
        BooleanQuestionEntity booleanQuestionEntity = new BooleanQuestionEntity();
        booleanQuestionEntity.setQuestionText(booleanQuestionCreationDTO.getQuestionText());
        return booleanQuestionEntity;
    }
}
