package org.tu.sofia.java.questionnaire.mappers.responses;

import org.tu.sofia.java.questionnaire.dto.responses.OpenResponseDTO;
import org.tu.sofia.java.questionnaire.dto.responses.OptionResponseDTO;
import org.tu.sofia.java.questionnaire.entities.responses.OpenResponseEntity;
import org.tu.sofia.java.questionnaire.entities.responses.OptionResponseEntity;

public class OpenResponseMapper {
    // From entity to DTO
    public static OpenResponseDTO toDto(OpenResponseEntity openResponseEntity) {
        if (openResponseEntity == null) {
            return null;
        }
        return new OpenResponseDTO(openResponseEntity.getResponseText());
    }
}
