package org.tu.sofia.java.questionnaire.dto.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BooleanQuestionWithResultsDTO extends QuestionDTO {
    private Integer trueVotes;
    private Integer falseVotes;

    public BooleanQuestionWithResultsDTO(String questionText) {
        super(questionText);
        this.trueVotes = 0;
        this.falseVotes = 0;
    }

    public BooleanQuestionWithResultsDTO(String questionText, Integer trueVotes, Integer falseVotes) {
        super(questionText);
        this.trueVotes = trueVotes;
        this.falseVotes = falseVotes;
    }
}
