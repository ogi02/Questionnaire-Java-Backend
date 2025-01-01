package org.tu.sofia.java.questionnaire.mappers;

import lombok.experimental.UtilityClass;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireDTO;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireWithResultsDTO;
import org.tu.sofia.java.questionnaire.dtos.questions.*; // NOPMD
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

@UtilityClass
public class QuestionnaireMapper {
    // From entity to DTO
    public static QuestionnaireDTO toDto(final QuestionnaireEntity questionnaireEntity) {
        if (questionnaireEntity == null) {
            return null;
        }

        // Map every boolean question from QuestionEntity to BooleanQuestionEntity
        final Set<BooleanQuestionEntity> booleanQuestionEntitySet = new HashSet<>();
        questionnaireEntity.getQuestions()
                .stream().filter(questionEntity -> questionEntity instanceof BooleanQuestionEntity)
                .forEach(questionEntity -> booleanQuestionEntitySet.add((BooleanQuestionEntity) questionEntity));
        // Map every BooleanQuestionEntity to BooleanQuestionDTO
        final Set<BooleanQuestionDTO> booleanQuestionDTOSet = booleanQuestionEntitySet
                .stream().map(BooleanQuestionMapper::toDto).collect(Collectors.toSet());

        // Map every open question from QuestionEntity to OpenQuestionEntity
        final Set<OpenQuestionEntity> openQuestionEntitySet = new HashSet<>();
        questionnaireEntity.getQuestions()
                .stream().filter(questionEntity -> questionEntity instanceof OpenQuestionEntity)
                .forEach(questionEntity -> openQuestionEntitySet.add((OpenQuestionEntity) questionEntity));
        // Map every OpenQuestionEntity to OpenQuestionDTO
        final Set<OpenQuestionDTO> openQuestionDTOSet = openQuestionEntitySet
                .stream().map(OpenQuestionMapper::toDto).collect(Collectors.toSet());

        // Map every option question from QuestionEntity to OptionQuestionEntity
        final Set<OptionQuestionEntity> optionQuestionEntitySet = new HashSet<>();
        questionnaireEntity.getQuestions()
                .stream().filter(questionEntity -> questionEntity instanceof OptionQuestionEntity)
                .forEach(questionEntity -> optionQuestionEntitySet.add((OptionQuestionEntity) questionEntity));
        // Map every OptionQuestionEntity to OptionQuestionDTO
        final Set<OptionQuestionDTO> optionQuestionDTOSet = optionQuestionEntitySet
                .stream().map(OptionQuestionMapper::toDto).collect(Collectors.toSet());

        return QuestionnaireDTO.builder()
                .withTitle(questionnaireEntity.getTitle())
                .withDescription(questionnaireEntity.getDescription())
                .withIsOpen(questionnaireEntity.getIsOpen())
                .withIsPublic(questionnaireEntity.getIsPublic())
                .withBooleanQuestionDTOSet(booleanQuestionDTOSet)
                .withOpenQuestionDTOSet(openQuestionDTOSet)
                .withOptionQuestionDTOSet(optionQuestionDTOSet)
                .build();
    }

    // From entity to DTO with results
    public static QuestionnaireWithResultsDTO toDtoWithResults(final QuestionnaireEntity questionnaireEntity) {
        if (questionnaireEntity == null) {
            return null;
        }

        // Map every boolean question from QuestionEntity to BooleanQuestionEntity
        final Set<BooleanQuestionEntity> booleanQuestionEntitySet = new HashSet<>();
        questionnaireEntity.getQuestions()
                .stream().filter(questionEntity -> questionEntity instanceof BooleanQuestionEntity)
                .forEach(questionEntity -> booleanQuestionEntitySet.add((BooleanQuestionEntity) questionEntity));
        // Map every BooleanQuestionEntity to BooleanQuestionDTO
        final Set<BooleanQuestionWithResultsDTO> booleanQuestionWithResults = booleanQuestionEntitySet
                .stream().map(BooleanQuestionMapper::toDtoWithResults).collect(Collectors.toSet());

        // Map every open question from QuestionEntity to OpenQuestionEntity
        final Set<OpenQuestionEntity> openQuestionEntitySet = new HashSet<>();
        questionnaireEntity.getQuestions()
                .stream().filter(questionEntity -> questionEntity instanceof OpenQuestionEntity)
                .forEach(questionEntity -> openQuestionEntitySet.add((OpenQuestionEntity) questionEntity));
        // Map every OpenQuestionEntity to OpenQuestionDTO
        final Set<OpenQuestionWithResultsDTO> openQuestionWithResults = openQuestionEntitySet
                .stream().map(OpenQuestionMapper::toDtoWithResults).collect(Collectors.toSet());

        // Map every option question from QuestionEntity to OptionQuestionEntity
        final Set<OptionQuestionEntity> optionQuestionEntitySet = new HashSet<>();
        questionnaireEntity.getQuestions()
                .stream().filter(questionEntity -> questionEntity instanceof OptionQuestionEntity)
                .forEach(questionEntity -> optionQuestionEntitySet.add((OptionQuestionEntity) questionEntity));
        // Map every OptionQuestionEntity to OptionQuestionDTO
        final Set<OptionQuestionWithResultsDTO> optionQuestionWithResults = optionQuestionEntitySet
                .stream().map(OptionQuestionMapper::toDtoWithResults).collect(Collectors.toSet());

        return QuestionnaireWithResultsDTO.builder()
                .withTitle(questionnaireEntity.getTitle())
                .withDescription(questionnaireEntity.getDescription())
                .withIsOpen(questionnaireEntity.getIsOpen())
                .withIsPublic(questionnaireEntity.getIsPublic())
                .withBooleanQuestionDTOSet(booleanQuestionWithResults)
                .withOpenQuestionDTOSet(openQuestionWithResults)
                .withOptionQuestionDTOSet(optionQuestionWithResults)
                .build();
    }

    // From DTO to entity
    public static QuestionnaireEntity toEntity(final QuestionnaireDTO questionnaireDTO) {
        if (questionnaireDTO == null) {
            return null;
        }
        final QuestionnaireEntity questionnaireEntity = QuestionnaireEntity.builder()
                .withTitle(questionnaireDTO.getTitle())
                .withDescription(questionnaireDTO.getDescription())
                .withIsOpen(questionnaireDTO.getIsOpen())
                .withIsPublic(questionnaireDTO.getIsPublic())
                .build();

        // Cast to child classes of the QuestionEntity
        final Set<QuestionEntity> questions = new HashSet<>();
        // Map every BooleanQuestionDTO to BooleanQuestionEntity
        final Set<BooleanQuestionEntity> booleanQuestionEntitySet = questionnaireDTO.getBooleanQuestionDTOSet()
                .stream().map(BooleanQuestionMapper::toEntity).collect(Collectors.toSet());
        // Link every boolean question to the questionnaire
        booleanQuestionEntitySet
                .forEach(booleanQuestionEntity -> booleanQuestionEntity.setQuestionnaire(questionnaireEntity));
        // Map every OpenQuestionDTO to OpenQuestionEntity
        final Set<OpenQuestionEntity> openQuestionEntitySet = questionnaireDTO.getOpenQuestionDTOSet()
                .stream().map(OpenQuestionMapper::toEntity).collect(Collectors.toSet());
        // Link every open question to the questionnaire
        openQuestionEntitySet
                .forEach(openQuestionEntity -> openQuestionEntity.setQuestionnaire(questionnaireEntity));
        // Map every OptionQuestionDTO to OptionQuestionEntity
        final Set<OptionQuestionEntity> optionQuestionEntitySet = questionnaireDTO.getOptionQuestionDTOSet()
                .stream().map(OptionQuestionMapper::toEntity).collect(Collectors.toSet());
        // Link every option question to the questionnaire
        optionQuestionEntitySet
                .forEach(optionQuestionEntity -> optionQuestionEntity.setQuestionnaire(questionnaireEntity));

        // Add all questions
        questions.addAll(booleanQuestionEntitySet);
        questions.addAll(openQuestionEntitySet);
        questions.addAll(optionQuestionEntitySet);
        questionnaireEntity.setQuestions(questions);

        return questionnaireEntity;
    }
}