package org.tu.sofia.java.questionnaire.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dto.questions.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class QuestionnaireCreationDTO {
    private String title;
    private String description;
    private Boolean isOpen;
    private Boolean isPublic;
    private Set<BooleanQuestionCreationDTO> booleanQuestionCreationDTOSet;
    private Set<OpenQuestionCreationDTO> openQuestionCreationDTOSet;
    private Set<OptionQuestionCreationDTO> optionQuestionCreationDTOSet;
}
