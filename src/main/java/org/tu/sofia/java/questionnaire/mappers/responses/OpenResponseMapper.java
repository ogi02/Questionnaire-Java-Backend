package org.tu.sofia.java.questionnaire.mappers.responses;

import lombok.experimental.UtilityClass;
import org.tu.sofia.java.questionnaire.dtos.responses.OpenResponseWithResultsDTO;
import org.tu.sofia.java.questionnaire.entities.responses.OpenResponseEntity;

@UtilityClass
public class OpenResponseMapper {
    // From entity to DTO
    public static OpenResponseWithResultsDTO toDto(final OpenResponseEntity openResponseEntity) {
        if (openResponseEntity == null) {
            return null;
        }
        return OpenResponseWithResultsDTO.builder().withResponseText(openResponseEntity.getResponseText()).build();
    }
}
