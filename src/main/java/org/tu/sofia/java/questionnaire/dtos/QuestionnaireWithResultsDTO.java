package org.tu.sofia.java.questionnaire.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dtos.questions.BooleanQuestionWithResultsDTO;
import org.tu.sofia.java.questionnaire.dtos.questions.OpenQuestionWithResultsDTO;
import org.tu.sofia.java.questionnaire.dtos.questions.OptionQuestionWithResultsDTO;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class QuestionnaireWithResultsDTO {
    private String title;
    private String description;
    private Boolean isOpen;
    private Boolean isPublic;
    private Set<BooleanQuestionWithResultsDTO> booleanQuestionsResults;
    private Set<OpenQuestionWithResultsDTO> openQuestionsResults;
    private Set<OptionQuestionWithResultsDTO> optionQuestionsResults;
}
