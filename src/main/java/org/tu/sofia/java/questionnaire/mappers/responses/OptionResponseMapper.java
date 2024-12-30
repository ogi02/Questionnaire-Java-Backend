package org.tu.sofia.java.questionnaire.mappers.responses;

import lombok.experimental.UtilityClass;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseDTO;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseWithResultsDTO;
import org.tu.sofia.java.questionnaire.entities.responses.OptionResponseEntity;

@UtilityClass
public class OptionResponseMapper {
    // From entity to DTO
    public static OptionResponseWithResultsDTO toDtoWithResults(final OptionResponseEntity optionResponseEntity) {
        if (optionResponseEntity == null) {
            return null;
        }
        return new OptionResponseWithResultsDTO(optionResponseEntity.getOption(), optionResponseEntity.getVotes());
    }
    // From entity to DTO without results
    public static OptionResponseDTO toDtoWithoutVotes(final OptionResponseEntity optionResponseEntity) {
        if (optionResponseEntity == null) {
            return null;
        }
        return new OptionResponseDTO(optionResponseEntity.getOption());
    }
    // From DTO to entity
    public static OptionResponseEntity toEntity(final OptionResponseDTO optionResponseDTO) {
        if (optionResponseDTO == null) {
            return null;
        }
        final OptionResponseEntity optionResponseEntity = new OptionResponseEntity();
        optionResponseEntity.setOption(optionResponseEntity.getOption());
        return optionResponseEntity;
    }
}
