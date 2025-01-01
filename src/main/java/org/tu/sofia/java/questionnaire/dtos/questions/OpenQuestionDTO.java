package org.tu.sofia.java.questionnaire.dtos.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpenQuestionDTO extends QuestionDTO {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OpenQuestionDTO openQuestion;

        public Builder() {
            this.openQuestion = new OpenQuestionDTO();
        }

        public Builder withQuestionText(final String questionText) {
            this.openQuestion.setQuestionText(questionText);
            return this;
        }

        public OpenQuestionDTO build() {
            return this.openQuestion;
        }
    }
}
