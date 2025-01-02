package org.tu.sofia.java.questionnaire.entities.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*; // NOPMD
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;

@Entity
@Table(name = "boolean_questions")
@Getter
@Setter
@NoArgsConstructor
public class BooleanQuestionEntity extends QuestionEntity {
    @Column
    private Integer trueVotes = 0;

    @Column
    private Integer falseVotes = 0;

    @Override
    public <T> void answerQuestion(final T response) throws IllegalArgumentException {
        if (response instanceof Boolean answer) {
            if (answer) {
                trueVotes++;
            } else {
                falseVotes++;
            }
        } else {
            throw new IllegalArgumentException("Invalid answer type for option question.");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final BooleanQuestionEntity booleanQuestion;

        public Builder() {
            this.booleanQuestion = new BooleanQuestionEntity();
        }

        public Builder withId(final Long id) {
            this.booleanQuestion.setId(id);
            return this;
        }

        public Builder withQuestionText(final String questionText) {
            this.booleanQuestion.setQuestionText(questionText);
            return this;
        }

        public Builder withQuestionnaire(final QuestionnaireEntity questionnaire) {
            this.booleanQuestion.setQuestionnaire(questionnaire);
            return this;
        }

        public Builder withTrueVotes(final Integer trueVotes) {
            this.booleanQuestion.setTrueVotes(trueVotes);
            return this;
        }

        public Builder withFalseVotes(final Integer falseVotes) {
            this.booleanQuestion.setFalseVotes(falseVotes);
            return this;
        }

        public BooleanQuestionEntity build() {
            return this.booleanQuestion;
        }
    }
}
