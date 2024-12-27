package org.tu.sofia.java.questionnaire.mappers.questions;

import org.tu.sofia.java.questionnaire.dtos.questions.OptionQuestionDTO;
import org.tu.sofia.java.questionnaire.dtos.questions.OptionQuestionWithResultsDTO;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseDTO;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseWithoutVotesDTO;
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
        // Map all responses to DTOs without votes
        Set<OptionResponseWithoutVotesDTO> optionResponseWithoutVotesDTOS = optionQuestionEntity.getOptions().stream().map(OptionResponseMapper::toDtoWithoutVotes).collect(Collectors.toSet());
        return new OptionQuestionDTO(optionQuestionEntity.getQuestionText(), optionResponseWithoutVotesDTOS);
    }
    // From entity to DTO with results
    public static OptionQuestionWithResultsDTO toDtoWithResults(OptionQuestionEntity optionQuestionEntity) {
        if (optionQuestionEntity == null) {
            return null;
        }
        // Map all responses to DTOs
        Set<OptionResponseDTO> optionResponseDTOSet = optionQuestionEntity.getOptions().stream().map(OptionResponseMapper::toDto).collect(Collectors.toSet());
        return new OptionQuestionWithResultsDTO(optionQuestionEntity.getQuestionText(), optionResponseDTOSet);
    }
    // From DTO to entity
    public static OptionQuestionEntity toEntity(OptionQuestionDTO optionQuestionDTO) {
        if (optionQuestionDTO == null) {
            return null;
        }
        OptionQuestionEntity optionQuestionEntity = new OptionQuestionEntity();
        // Map all responses to entities
        Set<OptionResponseEntity> optionResponseEntitySet = optionQuestionDTO.getOptionResponseWithoutVotesDTOSet().stream().map(OptionResponseMapper::toEntity).collect(Collectors.toSet());
        // Link options to the question
        optionResponseEntitySet.forEach(optionResponseEntity -> optionResponseEntity.setQuestion(optionQuestionEntity));
        // Create OptionQuestionEntity entity
        optionQuestionEntity.setQuestionText(optionQuestionDTO.getQuestionText());
        optionQuestionEntity.setOptions(optionResponseEntitySet);
        return optionQuestionEntity;
    }
}
