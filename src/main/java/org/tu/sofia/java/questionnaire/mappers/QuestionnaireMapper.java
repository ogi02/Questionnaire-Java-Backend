package org.tu.sofia.java.questionnaire.mappers;

import org.tu.sofia.java.questionnaire.dtos.QuestionnaireDTO;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireWithResultsDTO;
import org.tu.sofia.java.questionnaire.dtos.questions.*;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.questions.BooleanQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.questions.OpenQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.questions.OptionQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.questions.QuestionEntity;
import org.tu.sofia.java.questionnaire.mappers.questions.BooleanQuestionMapper;
import org.tu.sofia.java.questionnaire.mappers.questions.OpenQuestionMapper;
import org.tu.sofia.java.questionnaire.mappers.questions.OptionQuestionMapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestionnaireMapper {
    // From entity to DTO
    public static QuestionnaireDTO toDto(QuestionnaireEntity questionnaireEntity) {
        if (questionnaireEntity == null) {
            return null;
        }

        // Map every boolean question from QuestionEntity to BooleanQuestionEntity
        Set<BooleanQuestionEntity> booleanQuestionEntitySet = new HashSet<>();
        questionnaireEntity.getQuestions().stream().filter(questionEntity -> questionEntity instanceof BooleanQuestionEntity).forEach(questionEntity -> booleanQuestionEntitySet.add((BooleanQuestionEntity) questionEntity));
        // Map every BooleanQuestionEntity to BooleanQuestionDTO
        Set<BooleanQuestionDTO> booleanQuestionDTOSet = booleanQuestionEntitySet.stream().map(BooleanQuestionMapper::toDto).collect(Collectors.toSet());

        // Map every open question from QuestionEntity to OpenQuestionEntity
        Set<OpenQuestionEntity> openQuestionEntitySet = new HashSet<>();
        questionnaireEntity.getQuestions().stream().filter(questionEntity -> questionEntity instanceof OpenQuestionEntity).forEach(questionEntity -> openQuestionEntitySet.add((OpenQuestionEntity) questionEntity));
        // Map every OpenQuestionEntity to OpenQuestionDTO
        Set<OpenQuestionDTO> openQuestionDTOSet = openQuestionEntitySet.stream().map(OpenQuestionMapper::toDto).collect(Collectors.toSet());

        // Map every option question from QuestionEntity to OptionQuestionEntity
        Set<OptionQuestionEntity> optionQuestionEntitySet = new HashSet<>();
        questionnaireEntity.getQuestions().stream().filter(questionEntity -> questionEntity instanceof OptionQuestionEntity).forEach(questionEntity -> optionQuestionEntitySet.add((OptionQuestionEntity) questionEntity));
        // Map every OptionQuestionEntity to OptionQuestionDTO
        Set<OptionQuestionDTO> optionQuestionDTOSet = optionQuestionEntitySet.stream().map(OptionQuestionMapper::toDto).collect(Collectors.toSet());

        return new QuestionnaireDTO(
                questionnaireEntity.getTitle(),
                questionnaireEntity.getDescription(),
                questionnaireEntity.getIsOpen(),
                questionnaireEntity.getIsPublic(),
                booleanQuestionDTOSet,
                openQuestionDTOSet,
                optionQuestionDTOSet
        );
    }

    // From entity to DTO with results
    public static QuestionnaireWithResultsDTO toDtoWithResults(QuestionnaireEntity questionnaireEntity) {
        if (questionnaireEntity == null) {
            return null;
        }

        // Map every boolean question from QuestionEntity to BooleanQuestionEntity
        Set<BooleanQuestionEntity> booleanQuestionEntitySet = new HashSet<>();
        questionnaireEntity.getQuestions().stream().filter(questionEntity -> questionEntity instanceof BooleanQuestionEntity).forEach(questionEntity -> booleanQuestionEntitySet.add((BooleanQuestionEntity) questionEntity));
        // Map every BooleanQuestionEntity to BooleanQuestionDTO
        Set<BooleanQuestionWithResultsDTO> booleanQuestionWithResultsDTOSet = booleanQuestionEntitySet.stream().map(BooleanQuestionMapper::toDtoWithResults).collect(Collectors.toSet());

        // Map every open question from QuestionEntity to OpenQuestionEntity
        Set<OpenQuestionEntity> openQuestionEntitySet = new HashSet<>();
        questionnaireEntity.getQuestions().stream().filter(questionEntity -> questionEntity instanceof OpenQuestionEntity).forEach(questionEntity -> openQuestionEntitySet.add((OpenQuestionEntity) questionEntity));
        // Map every OpenQuestionEntity to OpenQuestionDTO
        Set<OpenQuestionWithResultsDTO> openQuestionWithResultsDTOSet = openQuestionEntitySet.stream().map(OpenQuestionMapper::toDtoWithResults).collect(Collectors.toSet());

        // Map every option question from QuestionEntity to OptionQuestionEntity
        Set<OptionQuestionEntity> optionQuestionEntitySet = new HashSet<>();
        questionnaireEntity.getQuestions().stream().filter(questionEntity -> questionEntity instanceof OptionQuestionEntity).forEach(questionEntity -> optionQuestionEntitySet.add((OptionQuestionEntity) questionEntity));
        // Map every OptionQuestionEntity to OptionQuestionDTO
        Set<OptionQuestionWithResultsDTO> optionQuestionWithResultsDTOSet = optionQuestionEntitySet.stream().map(OptionQuestionMapper::toDtoWithResults).collect(Collectors.toSet());

        return new QuestionnaireWithResultsDTO(
                questionnaireEntity.getTitle(),
                questionnaireEntity.getDescription(),
                questionnaireEntity.getIsOpen(),
                questionnaireEntity.getIsPublic(),
                booleanQuestionWithResultsDTOSet,
                openQuestionWithResultsDTOSet,
                optionQuestionWithResultsDTOSet
        );
    }

    // From DTO to entity
    public static QuestionnaireEntity toEntity(QuestionnaireDTO questionnaireDTO) {
        if (questionnaireDTO == null) {
            return null;
        }
        QuestionnaireEntity questionnaireEntity = new QuestionnaireEntity();
        questionnaireEntity.setTitle(questionnaireDTO.getTitle());
        questionnaireEntity.setDescription(questionnaireDTO.getDescription());
        questionnaireEntity.setIsOpen(questionnaireDTO.getIsOpen());
        questionnaireEntity.setIsPublic(questionnaireDTO.getIsPublic());

        // Cast to child classes of the QuestionEntity
        Set<QuestionEntity> questions = new HashSet<>();
        // Map every BooleanQuestionDTO to BooleanQuestionEntity
        Set<BooleanQuestionEntity> booleanQuestionEntitySet = questionnaireDTO.getBooleanQuestionDTOSet().stream().map(BooleanQuestionMapper::toEntity).collect(Collectors.toSet());
        // Link every boolean question to the questionnaire
        booleanQuestionEntitySet.forEach(booleanQuestionEntity -> booleanQuestionEntity.setQuestionnaire(questionnaireEntity));
        // Map every OpenQuestionDTO to OpenQuestionEntity
        Set<OpenQuestionEntity> openQuestionEntitySet = questionnaireDTO.getOpenQuestionDTOSet().stream().map(OpenQuestionMapper::toEntity).collect(Collectors.toSet());
        // Link every open question to the questionnaire
        openQuestionEntitySet.forEach(openQuestionEntity -> openQuestionEntity.setQuestionnaire(questionnaireEntity));
        // Map every OptionQuestionDTO to OptionQuestionEntity
        Set<OptionQuestionEntity> optionQuestionEntitySet = questionnaireDTO.getOptionQuestionDTOSet().stream().map(OptionQuestionMapper::toEntity).collect(Collectors.toSet());
        // Link every option question to the questionnaire
        optionQuestionEntitySet.forEach(optionQuestionEntity -> optionQuestionEntity.setQuestionnaire(questionnaireEntity));

        // Add all questions
        questions.addAll(booleanQuestionEntitySet);
        questions.addAll(openQuestionEntitySet);
        questions.addAll(optionQuestionEntitySet);
        questionnaireEntity.setQuestions(questions);

        return questionnaireEntity;
    }
}