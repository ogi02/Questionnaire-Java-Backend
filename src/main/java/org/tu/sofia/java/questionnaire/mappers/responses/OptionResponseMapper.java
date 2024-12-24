package org.tu.sofia.java.questionnaire.mappers.responses;

import org.tu.sofia.java.questionnaire.dto.responses.OptionResponseCreationDTO;
import org.tu.sofia.java.questionnaire.dto.responses.OptionResponseDTO;
import org.tu.sofia.java.questionnaire.entities.responses.OptionResponseEntity;

public class OptionResponseMapper {
    // From entity to DTO
    public static OptionResponseDTO toDto(OptionResponseEntity optionResponseEntity) {
        if (optionResponseEntity == null) {
            return null;
        }
        return new OptionResponseDTO(optionResponseEntity.getOption(), optionResponseEntity.getVotes());
    }
    // From DTO to entity
    public static OptionResponseEntity toEntity(OptionResponseCreationDTO optionResponseCreationDTO) {
        if (optionResponseCreationDTO == null) {
            return null;
        }
        OptionResponseEntity optionResponseEntity = new OptionResponseEntity();
        optionResponseEntity.setOption(optionResponseCreationDTO.getOption());
        return optionResponseEntity;
    }
}
