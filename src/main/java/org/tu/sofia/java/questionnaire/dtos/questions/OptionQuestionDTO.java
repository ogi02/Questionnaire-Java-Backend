package org.tu.sofia.java.questionnaire.dtos.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseWithoutVotesDTO;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OptionQuestionDTO extends QuestionDTO {
    private Set<OptionResponseWithoutVotesDTO> optionResponseDTOSet;

    public OptionQuestionDTO(final String questionText, final Set<OptionResponseWithoutVotesDTO> optionResponseDTOSet) {
        super(questionText);
        this.optionResponseDTOSet = optionResponseDTOSet;
    }
}
