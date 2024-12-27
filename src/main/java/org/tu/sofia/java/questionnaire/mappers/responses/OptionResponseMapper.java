package org.tu.sofia.java.questionnaire.mappers.responses;

import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseDTO;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseWithoutVotesDTO;
import org.tu.sofia.java.questionnaire.entities.responses.OptionResponseEntity;

public class OptionResponseMapper {
    // From entity to DTO
    public static OptionResponseDTO toDto(OptionResponseEntity optionResponseEntity) {
        if (optionResponseEntity == null) {
            return null;
        }
        return new OptionResponseDTO(optionResponseEntity.getOption(), optionResponseEntity.getVotes());
    }
    // From entity to DTO without results
    public static OptionResponseWithoutVotesDTO toDtoWithoutVotes(OptionResponseEntity optionResponseEntity) {
        if (optionResponseEntity == null) {
            return null;
        }
        return new OptionResponseWithoutVotesDTO(optionResponseEntity.getOption());
    }
    // From DTO to entity
    public static OptionResponseEntity toEntity(OptionResponseWithoutVotesDTO optionResponseWithoutVotesDTO) {
        if (optionResponseWithoutVotesDTO == null) {
            return null;
        }
        OptionResponseEntity optionResponseEntity = new OptionResponseEntity();
        optionResponseEntity.setOption(optionResponseWithoutVotesDTO.getOption());
        return optionResponseEntity;
    }
}
