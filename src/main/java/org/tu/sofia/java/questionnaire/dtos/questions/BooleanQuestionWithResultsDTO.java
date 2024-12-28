package org.tu.sofia.java.questionnaire.dtos.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BooleanQuestionWithResultsDTO extends QuestionDTO {
    private Integer trueVotes;
    private Integer falseVotes;

    public BooleanQuestionWithResultsDTO(final String questionText) {
        super(questionText);
        this.trueVotes = 0;
        this.falseVotes = 0;
    }

    public BooleanQuestionWithResultsDTO(final String questionText, final Integer trueVotes, final Integer falseVotes) {
        super(questionText);
        this.trueVotes = trueVotes;
        this.falseVotes = falseVotes;
    }
}
