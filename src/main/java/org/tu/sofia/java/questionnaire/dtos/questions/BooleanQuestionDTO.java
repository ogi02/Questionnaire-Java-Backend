package org.tu.sofia.java.questionnaire.dtos.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BooleanQuestionDTO extends QuestionDTO {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final BooleanQuestionDTO booleanQuestion;

        public Builder() {
            this.booleanQuestion = new BooleanQuestionDTO();
        }

        public Builder withQuestionText(final String questionText) {
            this.booleanQuestion.setQuestionText(questionText);
            return this;
        }

        public BooleanQuestionDTO build() {
            return this.booleanQuestion;
        }
    }
}
