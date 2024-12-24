package org.tu.sofia.java.questionnaire.mappers.questions;

import org.tu.sofia.java.questionnaire.dto.questions.OpenQuestionCreationDTO;
import org.tu.sofia.java.questionnaire.dto.questions.OpenQuestionDTO;
import org.tu.sofia.java.questionnaire.dto.responses.OpenResponseDTO;
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
    public static OpenQuestionDTO toDtoWithResults(OpenQuestionEntity openQuestionEntity) {
        if (openQuestionEntity == null) {
            return null;
        }
        // Map all answers to DTOs
        Set<OpenResponseDTO> openResponseDTOSet = openQuestionEntity.getAnswers().stream().map(OpenResponseMapper::toDto).collect(Collectors.toSet());
        return new OpenQuestionDTO(openQuestionEntity.getQuestionText(), openResponseDTOSet);
    }
    // From DTO to entity
    public static OpenQuestionEntity toEntity(OpenQuestionCreationDTO openQuestionCreationDTO) {
        if (openQuestionCreationDTO == null) {
            return null;
        }
        OpenQuestionEntity openQuestionEntity = new OpenQuestionEntity();
        openQuestionEntity.setQuestionText(openQuestionCreationDTO.getQuestionText());
        return openQuestionEntity;
    }
}
