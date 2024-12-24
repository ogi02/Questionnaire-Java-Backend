package org.tu.sofia.java.questionnaire.dto.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dto.responses.OptionResponseCreationDTO;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OptionQuestionCreationDTO extends QuestionDTO {
    private Set<OptionResponseCreationDTO> optionResponseCreationDTOSet;

    public OptionQuestionCreationDTO(String questionText) {
        super(questionText);
        this.optionResponseCreationDTOSet = new HashSet<>();
    }

    public OptionQuestionCreationDTO(String questionText, Set<OptionResponseCreationDTO> optionResponseCreationDTOSet) {
        super(questionText);
        this.optionResponseCreationDTOSet = optionResponseCreationDTOSet;
    }
}
