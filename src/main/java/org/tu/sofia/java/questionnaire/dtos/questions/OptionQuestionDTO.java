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
    private Set<OptionResponseWithoutVotesDTO> optionResponseWithoutVotesDTOSet;

    public OptionQuestionDTO(String questionText, Set<OptionResponseWithoutVotesDTO> optionResponseWithoutVotesDTOSet) {
        super(questionText);
        this.optionResponseWithoutVotesDTOSet = optionResponseWithoutVotesDTOSet;
    }
}
