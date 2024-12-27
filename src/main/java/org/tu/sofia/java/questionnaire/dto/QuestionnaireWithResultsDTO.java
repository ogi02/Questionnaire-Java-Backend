package org.tu.sofia.java.questionnaire.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dto.questions.BooleanQuestionWithResultsDTO;
import org.tu.sofia.java.questionnaire.dto.questions.OpenQuestionWithResultsDTO;
import org.tu.sofia.java.questionnaire.dto.questions.OptionQuestionWithResultsDTO;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class QuestionnaireWithResultsDTO {
    private String title;
    private String description;
    private Boolean isOpen;
    private Boolean isPublic;
    private Set<BooleanQuestionWithResultsDTO> booleanQuestionWithResultsDTOSet;
    private Set<OpenQuestionWithResultsDTO> openQuestionWithResultsDTOSet;
    private Set<OptionQuestionWithResultsDTO> optionQuestionWithResultsDTOSet;
}
