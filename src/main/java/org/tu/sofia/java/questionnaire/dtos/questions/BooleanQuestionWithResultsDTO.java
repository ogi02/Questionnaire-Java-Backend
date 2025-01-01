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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final BooleanQuestionWithResultsDTO booleanQuestionWithResults;

        public Builder() {
            this.booleanQuestionWithResults = new BooleanQuestionWithResultsDTO();
        }

        public Builder withQuestionText(final String questionText) {
            this.booleanQuestionWithResults.setQuestionText(questionText);
            return this;
        }

        public Builder withTrueVotes(final Integer trueVotes) {
            this.booleanQuestionWithResults.setTrueVotes(trueVotes);
            return this;
        }

        public Builder withFalseVotes(final Integer falseVotes) {
            this.booleanQuestionWithResults.setFalseVotes(falseVotes);
            return this;
        }

        public BooleanQuestionWithResultsDTO build() {
            return this.booleanQuestionWithResults;
        }
    }
}
