package org.tu.sofia.java.questionnaire.dto.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dto.responses.OptionResponseDTO;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OptionQuestionDTO extends QuestionDTO {
    private Set<OptionResponseDTO> optionResponseDTOSet;

    public OptionQuestionDTO(String questionText) {
        super(questionText);
        this.optionResponseDTOSet = new HashSet<>();
    }

    public OptionQuestionDTO(String questionText, Set<OptionResponseDTO> optionResponseDTOSet) {
        super(questionText);
        this.optionResponseDTOSet = optionResponseDTOSet;
    }
}
