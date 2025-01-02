package org.tu.sofia.java.questionnaire.unit.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireDTO;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireWithResultsDTO;
import org.tu.sofia.java.questionnaire.dtos.questions.*; // NOPMD
import org.tu.sofia.java.questionnaire.dtos.responses.OpenResponseWithResultsDTO;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseDTO;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseWithResultsDTO;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.entities.questions.BooleanQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.questions.OpenQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.questions.OptionQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.responses.OptionResponseEntity;

import java.util.HashSet;
import java.util.Set;

@Component
public final class QuestionnaireCreator {

    private static String title;
    private static String description;
    private static Boolean isOpen;
    private static Boolean isPublic;
    private static String booleanQuestionText;
    private static String openQuestionText;
    private static String optionQuestionText;
    private static String openQuestionResponseText;
    private static String optionQuestionResponseText;

    private QuestionnaireCreator() { }

    @Value("${unit.test.questionnaire.title}")
    public void setTitle(final String title) {
        QuestionnaireCreator.title = title;
    }

    @Value("${unit.test.questionnaire.description}")
    public void setDescription(final String description) {
        QuestionnaireCreator.description = description;
    }

    @Value("${unit.test.questionnaire.isOpen}")
    public void setIsOpen(final Boolean isOpen) {
        QuestionnaireCreator.isOpen = isOpen;
    }

    @Value("${unit.test.questionnaire.isPublic}")
    public void setIsPublic(final Boolean isPublic) {
        QuestionnaireCreator.isPublic = isPublic;
    }

    @Value("${unit.test.questionnaire.boolean.question.text}")
    public void setBooleanQuestionText(final String booleanQuestionText) {
        QuestionnaireCreator.booleanQuestionText = booleanQuestionText;
    }

    @Value("${unit.test.questionnaire.open.question.text}")
    public void setOpenQuestionText(final String openQuestionText) {
        QuestionnaireCreator.openQuestionText = openQuestionText;
    }

    @Value("${unit.test.questionnaire.option.question.text}")
    public void setOptionQuestionText(final String optionQuestionText) {
        QuestionnaireCreator.optionQuestionText = optionQuestionText;
    }

    @Value("${unit.test.questionnaire.open.question.response.text}")
    public void setOpenQuestionResponseText(final String openQuestionResponseText) {
        QuestionnaireCreator.openQuestionResponseText = openQuestionResponseText;
    }

    @Value("${unit.test.questionnaire.option.question.response.text}")
    public void setOptionQuestionResponseText(final String optionQuestionResponseText) {
        QuestionnaireCreator.optionQuestionResponseText = optionQuestionResponseText;
    }

    public static QuestionnaireDTO.Builder createDefaultDTO() {
        // Default Questionnaire DTO without results
        return QuestionnaireDTO.builder()
                .withTitle(title)
                .withDescription(description)
                .withIsOpen(isOpen)
                .withIsPublic(isPublic)
                .withBooleanQuestionDTOSet(new HashSet<>() {{
                    add(BooleanQuestionDTO.builder()
                            .withQuestionText("%s %s".formatted(booleanQuestionText, 1))
                            .build());
                    add(BooleanQuestionDTO.builder()
                            .withQuestionText("%s %s".formatted(booleanQuestionText, 2))
                            .build());
                    add(BooleanQuestionDTO.builder()
                            .withQuestionText("%s %s".formatted(booleanQuestionText, 3))
                            .build());

                }})
                .withOpenQuestionDTOSet(new HashSet<>() {{
                    add(OpenQuestionDTO.builder()
                            .withQuestionText("%s %s".formatted(openQuestionText, 1))
                            .build());
                    add(OpenQuestionDTO.builder()
                            .withQuestionText("%s %s".formatted(openQuestionText, 2))
                            .build());
                    add(OpenQuestionDTO.builder()
                            .withQuestionText("%s %s".formatted(openQuestionText, 3))
                            .build());
                }})
                .withOptionQuestionDTOSet(new HashSet<>() {{
                    add(OptionQuestionDTO.builder()
                            .withQuestionText("%s %s".formatted(optionQuestionText, 1))
                            .withOptionResponseDTOSet(new HashSet<>(){{
                                add(OptionResponseDTO.builder()
                                        .withOption("%s %s". formatted(optionQuestionResponseText, 1)).build());
                                add(OptionResponseDTO.builder()
                                        .withOption("%s %s". formatted(optionQuestionResponseText, 2)).build());
                                add(OptionResponseDTO.builder()
                                        .withOption("%s %s". formatted(optionQuestionResponseText, 3)).build());
                            }})
                            .build());
                    add(OptionQuestionDTO.builder()
                            .withQuestionText("%s %s".formatted(optionQuestionText, 2))
                            .withOptionResponseDTOSet(new HashSet<>() {{
                                add(OptionResponseDTO.builder()
                                        .withOption("%s %s". formatted(optionQuestionResponseText, 1)).build());
                                add(OptionResponseDTO.builder()
                                        .withOption("%s %s". formatted(optionQuestionResponseText, 2)).build());
                                add(OptionResponseDTO.builder()
                                        .withOption("%s %s". formatted(optionQuestionResponseText, 3)).build());
                            }})
                            .build());
                    add(OptionQuestionDTO.builder()
                            .withQuestionText("%s %s".formatted(optionQuestionText, 3))
                            .withOptionResponseDTOSet(new HashSet<>() {{
                                add(OptionResponseDTO.builder()
                                        .withOption("%s %s". formatted(optionQuestionResponseText, 1)).build());
                                add(OptionResponseDTO.builder()
                                        .withOption("%s %s". formatted(optionQuestionResponseText, 2)).build());
                                add(OptionResponseDTO.builder()
                                        .withOption("%s %s". formatted(optionQuestionResponseText, 3)).build());
                            }})
                            .build());
                }});
    }

    public static QuestionnaireWithResultsDTO.Builder createDefaultDTOWithResults() {
        // Default Questionnaire DTO with results
        return QuestionnaireWithResultsDTO.builder()
                .withTitle(title)
                .withDescription(description)
                .withIsOpen(isOpen)
                .withIsPublic(isPublic)
                .withBooleanQuestionDTOSet(new HashSet<>() {{
                    add(BooleanQuestionWithResultsDTO.builder()
                            .withQuestionText("%s %s".formatted(booleanQuestionText, 1))
                            .withTrueVotes(3)
                            .withFalseVotes(3)
                            .build());
                    add(BooleanQuestionWithResultsDTO.builder()
                            .withQuestionText("%s %s".formatted(booleanQuestionText, 2))
                            .withTrueVotes(3)
                            .withFalseVotes(3)
                            .build());
                    add(BooleanQuestionWithResultsDTO.builder()
                            .withQuestionText("%s %s".formatted(booleanQuestionText, 3))
                            .withTrueVotes(3)
                            .withFalseVotes(3)
                            .build());

                }})
                .withOpenQuestionDTOSet(new HashSet<>() {{
                    add(OpenQuestionWithResultsDTO.builder()
                            .withQuestionText("%s %s".formatted(openQuestionText, 1))
                            .withOpenResponseWithResultsDTOSet(new HashSet<>(){{
                                add(OpenResponseWithResultsDTO.builder()
                                        .withResponseText("%s %s". formatted(openQuestionResponseText, 1))
                                        .build());
                                add(OpenResponseWithResultsDTO.builder()
                                        .withResponseText("%s %s". formatted(openQuestionResponseText, 2))
                                        .build());
                                add(OpenResponseWithResultsDTO.builder()
                                        .withResponseText("%s %s". formatted(openQuestionResponseText, 3))
                                        .build());
                            }})
                            .build());
                    add(OpenQuestionWithResultsDTO.builder()
                            .withQuestionText("%s %s".formatted(openQuestionText, 2))
                            .withOpenResponseWithResultsDTOSet(new HashSet<>(){{
                                add(OpenResponseWithResultsDTO.builder()
                                        .withResponseText("%s %s". formatted(openQuestionResponseText, 1))
                                        .build());
                                add(OpenResponseWithResultsDTO.builder()
                                        .withResponseText("%s %s". formatted(openQuestionResponseText, 2))
                                        .build());
                                add(OpenResponseWithResultsDTO.builder()
                                        .withResponseText("%s %s". formatted(openQuestionResponseText, 3))
                                        .build());
                            }})
                            .build());
                    add(OpenQuestionWithResultsDTO.builder()
                            .withQuestionText("%s %s".formatted(openQuestionText, 3))
                            .withOpenResponseWithResultsDTOSet(new HashSet<>(){{
                                add(OpenResponseWithResultsDTO.builder()
                                        .withResponseText("%s %s". formatted(openQuestionResponseText, 1))
                                        .build());
                                add(OpenResponseWithResultsDTO.builder()
                                        .withResponseText("%s %s". formatted(openQuestionResponseText, 2))
                                        .build());
                                add(OpenResponseWithResultsDTO.builder()
                                        .withResponseText("%s %s". formatted(openQuestionResponseText, 3))
                                        .build());
                            }})
                            .build());
                }})
                .withOptionQuestionDTOSet(new HashSet<>() {{
                    add(OptionQuestionWithResultsDTO.builder()
                            .withQuestionText("%s %s".formatted(openQuestionText, 1))
                            .withOptionResponseWithResultsDTOSet(new HashSet<>(){{
                                add(OptionResponseWithResultsDTO.builder()
                                        .withOption("%s %s". formatted(openQuestionResponseText, 1))
                                        .withVotes(3)
                                        .build());
                                add(OptionResponseWithResultsDTO.builder()
                                        .withOption("%s %s". formatted(openQuestionResponseText, 2))
                                        .withVotes(3)
                                        .build());
                                add(OptionResponseWithResultsDTO.builder()
                                        .withOption("%s %s". formatted(openQuestionResponseText, 3))
                                        .withVotes(3)
                                        .build());
                            }})
                            .build());
                    add(OptionQuestionWithResultsDTO.builder()
                            .withQuestionText("%s %s".formatted(openQuestionText, 2))
                            .withOptionResponseWithResultsDTOSet(new HashSet<>(){{
                                add(OptionResponseWithResultsDTO.builder()
                                        .withOption("%s %s". formatted(openQuestionResponseText, 1))
                                        .withVotes(3)
                                        .build());
                                add(OptionResponseWithResultsDTO.builder()
                                        .withOption("%s %s". formatted(openQuestionResponseText, 2))
                                        .withVotes(3)
                                        .build());
                                add(OptionResponseWithResultsDTO.builder()
                                        .withOption("%s %s". formatted(openQuestionResponseText, 3))
                                        .withVotes(3)
                                        .build());
                            }})
                            .build());
                    add(OptionQuestionWithResultsDTO.builder()
                            .withQuestionText("%s %s".formatted(openQuestionText, 3))
                            .withOptionResponseWithResultsDTOSet(new HashSet<>(){{
                                add(OptionResponseWithResultsDTO.builder()
                                        .withOption("%s %s". formatted(openQuestionResponseText, 1))
                                        .withVotes(3)
                                        .build());
                                add(OptionResponseWithResultsDTO.builder()
                                        .withOption("%s %s". formatted(openQuestionResponseText, 2))
                                        .withVotes(3)
                                        .build());
                                add(OptionResponseWithResultsDTO.builder()
                                        .withOption("%s %s". formatted(openQuestionResponseText, 3))
                                        .withVotes(3)
                                        .build());
                            }})
                            .build());
                }});
    }

    public static QuestionnaireEntity createDefaultEntity(final UserEntity owner, final Boolean withIDs) {
        // Questionnaire entity
        final QuestionnaireEntity questionnaire = QuestionnaireEntity.builder()
                .withId(withIDs ? 1L : null)
                .withTitle(title)
                .withDescription(description)
                .withOwner(owner)
                .withAdministrators(new HashSet<>(){{add(owner);}})
                .withIsOpen(isOpen)
                .withIsPublic(isPublic)
                .build();

        // Boolean Question Entities
        final Set<BooleanQuestionEntity> booleanQuestionEntities = getBooleanQuestionEntities(withIDs, questionnaire);

        // Open Question Entities
        final Set<OpenQuestionEntity> openQuestionEntities = getOpenQuestionEntities(withIDs, questionnaire);

        // Option Question Entities
        final Set<OptionQuestionEntity> optionQuestionEntities = getOptionQuestionEntities(withIDs, questionnaire);

        // Add questions to questionnaire
        questionnaire.setQuestions(new HashSet<>() {{
            addAll(booleanQuestionEntities);
            addAll(openQuestionEntities);
            addAll(optionQuestionEntities);
        }});

        return questionnaire;
    }

    private static Set<BooleanQuestionEntity> getBooleanQuestionEntities(
            final Boolean withIDs, final QuestionnaireEntity questionnaire) {
        return new HashSet<>() {{
            add(BooleanQuestionEntity.builder()
                    .withId(withIDs ? 1L : null)
                    .withQuestionText("%s %s".formatted(booleanQuestionText, 1))
                    .withQuestionnaire(questionnaire)
                    .build());
            add(BooleanQuestionEntity.builder()
                    .withId(withIDs ? 2L : null)
                    .withQuestionText("%s %s".formatted(booleanQuestionText, 2))
                    .withQuestionnaire(questionnaire)
                    .build());
            add(BooleanQuestionEntity.builder()
                    .withId(withIDs ? 3L : null)
                    .withQuestionText("%s %s".formatted(booleanQuestionText, 3))
                    .withQuestionnaire(questionnaire)
                    .build());
        }};
    }

    private static Set<OpenQuestionEntity> getOpenQuestionEntities(
            final Boolean withIDs, final QuestionnaireEntity questionnaire) {
        return new HashSet<>() {{
            add(OpenQuestionEntity.builder()
                    .withId(withIDs ? 4L : null)
                    .withQuestionText("%s %s".formatted(openQuestionText, 1))
                    .withQuestionnaire(questionnaire)
                    .build());
            add(OpenQuestionEntity.builder()
                    .withId(withIDs ? 5L : null)
                    .withQuestionText("%s %s".formatted(openQuestionText, 2))
                    .withQuestionnaire(questionnaire)
                    .build());
            add(OpenQuestionEntity.builder()
                    .withId(withIDs ? 6L : null)
                    .withQuestionText("%s %s".formatted(openQuestionText, 3))
                    .withQuestionnaire(questionnaire)
                    .build());
        }};
    }

    private static Set<OptionQuestionEntity> getOptionQuestionEntities(
            final Boolean withIDs, final QuestionnaireEntity questionnaire) {
        final Set<OptionQuestionEntity> optionQuestionEntities = new HashSet<>() {{
            add(OptionQuestionEntity.builder()
                    .withId(withIDs ? 7L : null)
                    .withQuestionText("%s %s".formatted(optionQuestionText, 1))
                    .withQuestionnaire(questionnaire)
                    .withOptions(getOptionResponseEntities(withIDs, 0L))
                    .build());
            add(OptionQuestionEntity.builder()
                    .withId(withIDs ? 8L : null)
                    .withQuestionText("%s %s".formatted(optionQuestionText, 2))
                    .withQuestionnaire(questionnaire)
                    .withOptions(getOptionResponseEntities(withIDs, 1L))
                    .build());
            add(OptionQuestionEntity.builder()
                    .withId(withIDs ? 9L : null)
                    .withQuestionText("%s %s".formatted(optionQuestionText, 3))
                    .withQuestionnaire(questionnaire)
                    .withOptions(getOptionResponseEntities(withIDs, 2L))
                    .build());
        }};
        // Add Option Question Entities to the respective Option Response Entities
        optionQuestionEntities
                .forEach(optionQuestionEntity -> optionQuestionEntity.getOptions()
                        .forEach(optionResponseEntity -> optionResponseEntity.setQuestion(optionQuestionEntity)));
        return optionQuestionEntities;
    }

    private static Set<OptionResponseEntity> getOptionResponseEntities(final Boolean withIDs, final Long index) {
        return new HashSet<>(){{
            add(OptionResponseEntity.builder()
                    .withId(withIDs ? 3L * index + 1L : null)
                    .withOption("%s %s".formatted(optionQuestionResponseText, 1))
                    .build());
            add(OptionResponseEntity.builder()
                    .withId(withIDs ? 3L * index + 2L : null)
                    .withOption("%s %s".formatted(optionQuestionResponseText, 2))
                    .build());
            add(OptionResponseEntity.builder()
                    .withId(withIDs ? 3L * index + 3L : null)
                    .withOption("%s %s".formatted(optionQuestionResponseText, 3))
                    .build());
        }};
    }
}
