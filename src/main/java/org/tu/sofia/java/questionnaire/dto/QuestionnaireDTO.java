package org.tu.sofia.java.questionnaire.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dto.questions.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class QuestionnaireDTO {
    private String title;
    private String description;
    private Boolean isOpen;
    private Boolean isPublic;
    private Set<BooleanQuestionDTO> booleanQuestionDTOSet;
    private Set<OpenQuestionDTO> openQuestionDTOSet;
    private Set<OptionQuestionDTO> optionQuestionDTOSet;
}
