package org.tu.sofia.java.questionnaire.mappers.questions;

import lombok.experimental.UtilityClass;
import org.tu.sofia.java.questionnaire.dtos.questions.OptionQuestionDTO;
import org.tu.sofia.java.questionnaire.dtos.questions.OptionQuestionWithResultsDTO;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseDTO;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseWithResultsDTO;
import org.tu.sofia.java.questionnaire.entities.questions.OptionQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.responses.OptionResponseEntity;
import org.tu.sofia.java.questionnaire.mappers.responses.OptionResponseMapper;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class OptionQuestionMapper {
    // From entity to DTO
    public static OptionQuestionDTO toDto(final OptionQuestionEntity optionQuestionEntity) {
        if (optionQuestionEntity == null) {
            return null;
        }
        // Map all responses to DTOs without votes
        final Set<OptionResponseDTO> optionResponseDTOS = optionQuestionEntity.getOptions()
                .stream().map(OptionResponseMapper::toDtoWithoutVotes).collect(Collectors.toSet());
        return new OptionQuestionDTO(optionQuestionEntity.getQuestionText(), optionResponseDTOS);
    }
    // From entity to DTO with results
    public static OptionQuestionWithResultsDTO toDtoWithResults(final OptionQuestionEntity optionQuestionEntity) {
        if (optionQuestionEntity == null) {
            return null;
        }
        // Map all responses to DTOs
        final Set<OptionResponseWithResultsDTO> optionResponseWithResultsDTOSet = optionQuestionEntity.getOptions()
                .stream().map(OptionResponseMapper::toDtoWithResults).collect(Collectors.toSet());
        return new
                OptionQuestionWithResultsDTO(optionQuestionEntity.getQuestionText(), optionResponseWithResultsDTOSet);
    }
    // From DTO to entity
    public static OptionQuestionEntity toEntity(final OptionQuestionDTO optionQuestionDTO) {
        if (optionQuestionDTO == null) {
            return null;
        }
        final OptionQuestionEntity optionQuestionEntity = new OptionQuestionEntity();
        // Map all responses to entities
        final Set<OptionResponseEntity> optionResponseEntitySet = optionQuestionDTO.getOptionResponseDTOSet()
                .stream().map(OptionResponseMapper::toEntity).collect(Collectors.toSet());
        // Link options to the question
        optionResponseEntitySet.forEach(optionResponseEntity -> optionResponseEntity.setQuestion(optionQuestionEntity));
        // Create OptionQuestionEntity entity
        optionQuestionEntity.setQuestionText(optionQuestionDTO.getQuestionText());
        optionQuestionEntity.setOptions(optionResponseEntitySet);
        return optionQuestionEntity;
    }
}
