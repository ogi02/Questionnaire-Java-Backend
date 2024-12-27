package org.tu.sofia.java.questionnaire.mappers.responses;

import org.tu.sofia.java.questionnaire.dtos.responses.OpenResponseDTO;
import org.tu.sofia.java.questionnaire.entities.responses.OpenResponseEntity;

public class OpenResponseMapper {
    // From entity to DTO
    public static OpenResponseDTO toDto(OpenResponseEntity openResponseEntity) {
        if (openResponseEntity == null) {
            return null;
        }
        return new OpenResponseDTO(openResponseEntity.getResponseText());
    }
}
