package org.tu.sofia.java.questionnaire.mappers.questions;

import org.tu.sofia.java.questionnaire.dtos.questions.OpenQuestionDTO;
import org.tu.sofia.java.questionnaire.dtos.questions.OpenQuestionWithResultsDTO;
import org.tu.sofia.java.questionnaire.dtos.responses.OpenResponseDTO;
import org.tu.sofia.java.questionnaire.entities.questions.OpenQuestionEntity;
import org.tu.sofia.java.questionnaire.mappers.responses.OpenResponseMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class OpenQuestionMapper {
    // From entity to DTO
    public static OpenQuestionDTO toDto(OpenQuestionEntity openQuestionEntity) {
        if (openQuestionEntity == null) {
            return null;
        }
        return new OpenQuestionDTO(openQuestionEntity.getQuestionText());
    }
    // From entity to DTO with results
    public static OpenQuestionWithResultsDTO toDtoWithResults(OpenQuestionEntity openQuestionEntity) {
        if (openQuestionEntity == null) {
            return null;
        }
        // Map all answers to DTOs
        Set<OpenResponseDTO> openResponseDTOSet = openQuestionEntity.getAnswers().stream().map(OpenResponseMapper::toDto).collect(Collectors.toSet());
        return new OpenQuestionWithResultsDTO(openQuestionEntity.getQuestionText(), openResponseDTOSet);
    }
    // From DTO to entity
    public static OpenQuestionEntity toEntity(OpenQuestionDTO openQuestionDTO) {
        if (openQuestionDTO == null) {
            return null;
        }
        OpenQuestionEntity openQuestionEntity = new OpenQuestionEntity();
        openQuestionEntity.setQuestionText(openQuestionDTO.getQuestionText());
        return openQuestionEntity;
    }
}
