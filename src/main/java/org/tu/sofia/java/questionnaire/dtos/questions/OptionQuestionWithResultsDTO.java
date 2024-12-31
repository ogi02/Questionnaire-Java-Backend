package org.tu.sofia.java.questionnaire.dtos.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseWithResultsDTO;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OptionQuestionWithResultsDTO extends QuestionDTO {
    private Set<OptionResponseWithResultsDTO> optionResponseWithResultsDTOSet;

    public OptionQuestionWithResultsDTO(final String questionText) {
        super(questionText);
        this.optionResponseWithResultsDTOSet = new HashSet<>();
    }

    public OptionQuestionWithResultsDTO(final String questionText,
                                        final Set<OptionResponseWithResultsDTO> optionResponseWithResultsDTOSet) {
        super(questionText);
        this.optionResponseWithResultsDTOSet = optionResponseWithResultsDTOSet;
    }
}
