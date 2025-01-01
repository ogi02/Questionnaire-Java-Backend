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
        return OptionResponseWithResultsDTO.builder()
                .withOption(optionResponseEntity.getOption())
                .withVotes(optionResponseEntity.getVotes())
                .build();
    }
    // From entity to DTO without results
    public static OptionResponseDTO toDtoWithoutVotes(final OptionResponseEntity optionResponseEntity) {
        if (optionResponseEntity == null) {
            return null;
        }
        return OptionResponseDTO.builder().withOption(optionResponseEntity.getOption()).build();
    }
    // From DTO to entity
    public static OptionResponseEntity toEntity(final OptionResponseDTO optionResponseDTO) {
        if (optionResponseDTO == null) {
            return null;
        }
        return OptionResponseEntity.builder().withOption(optionResponseDTO.getOption()).build();
    }
}
