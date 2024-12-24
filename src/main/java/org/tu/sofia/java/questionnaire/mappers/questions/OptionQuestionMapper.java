package org.tu.sofia.java.questionnaire.mappers.questions;

import org.tu.sofia.java.questionnaire.dto.questions.OptionQuestionCreationDTO;
import org.tu.sofia.java.questionnaire.dto.questions.OptionQuestionDTO;
import org.tu.sofia.java.questionnaire.dto.responses.OptionResponseDTO;
import org.tu.sofia.java.questionnaire.entities.questions.OptionQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.responses.OptionResponseEntity;
import org.tu.sofia.java.questionnaire.mappers.responses.OptionResponseMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class OptionQuestionMapper {
    // From entity to DTO
    public static OptionQuestionDTO toDto(OptionQuestionEntity optionQuestionEntity) {
        if (optionQuestionEntity == null) {
            return null;
        }
        return new OptionQuestionDTO(optionQuestionEntity.getQuestionText());
    }
    // From entity to DTO with results
    public static OptionQuestionDTO toDtoWithResults(OptionQuestionEntity optionQuestionEntity) {
        if (optionQuestionEntity == null) {
            return null;
        }
        // Map all responses to DTOs
        Set<OptionResponseDTO> optionResponseDTOSet = optionQuestionEntity.getOptions().stream().map(OptionResponseMapper::toDto).collect(Collectors.toSet());
        return new OptionQuestionDTO(optionQuestionEntity.getQuestionText(), optionResponseDTOSet);
    }
    // From DTO to entity
    public static OptionQuestionEntity toEntity(OptionQuestionCreationDTO optionQuestionCreationDTO) {
        if (optionQuestionCreationDTO == null) {
            return null;
        }
        OptionQuestionEntity optionQuestionEntity = new OptionQuestionEntity();
        // Map all responses to entities
        Set<OptionResponseEntity> optionResponseEntitySet = optionQuestionCreationDTO.getOptionResponseCreationDTOSet().stream().map(OptionResponseMapper::toEntity).collect(Collectors.toSet());
        // Link options to the question
        optionResponseEntitySet.forEach(optionResponseEntity -> optionResponseEntity.setQuestion(optionQuestionEntity));
        // Create OptionQuestionEntity entity
        optionQuestionEntity.setQuestionText(optionQuestionCreationDTO.getQuestionText());
        optionQuestionEntity.setOptions(optionResponseEntitySet);
        return optionQuestionEntity;
    }
}
