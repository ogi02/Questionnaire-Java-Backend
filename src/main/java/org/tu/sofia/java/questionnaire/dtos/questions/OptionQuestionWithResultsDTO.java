package org.tu.sofia.java.questionnaire.dtos.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseDTO;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OptionQuestionWithResultsDTO extends QuestionDTO {
    private Set<OptionResponseDTO> optionResponseDTOSet;

    public OptionQuestionWithResultsDTO(String questionText) {
        super(questionText);
        this.optionResponseDTOSet = new HashSet<>();
    }

    public OptionQuestionWithResultsDTO(String questionText, Set<OptionResponseDTO> optionResponseDTOSet) {
        super(questionText);
        this.optionResponseDTOSet = optionResponseDTOSet;
    }
}
