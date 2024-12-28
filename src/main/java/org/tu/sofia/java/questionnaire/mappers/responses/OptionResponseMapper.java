package org.tu.sofia.java.questionnaire.mappers.responses;

import lombok.experimental.UtilityClass;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseDTO;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseWithoutVotesDTO;
import org.tu.sofia.java.questionnaire.entities.responses.OptionResponseEntity;

@UtilityClass
public class OptionResponseMapper {
    // From entity to DTO
    public static OptionResponseDTO toDto(final OptionResponseEntity optionResponseEntity) {
        if (optionResponseEntity == null) {
            return null;
        }
        return new OptionResponseDTO(optionResponseEntity.getOption(), optionResponseEntity.getVotes());
    }
    // From entity to DTO without results
    public static OptionResponseWithoutVotesDTO toDtoWithoutVotes(final OptionResponseEntity optionResponseEntity) {
        if (optionResponseEntity == null) {
            return null;
        }
        return new OptionResponseWithoutVotesDTO(optionResponseEntity.getOption());
    }
    // From DTO to entity
    public static OptionResponseEntity toEntity(final OptionResponseWithoutVotesDTO optionResponseDTO) {
        if (optionResponseDTO == null) {
            return null;
        }
        final OptionResponseEntity optionResponseEntity = new OptionResponseEntity();
        optionResponseEntity.setOption(optionResponseEntity.getOption());
        return optionResponseEntity;
    }
}
