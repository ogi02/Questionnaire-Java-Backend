package org.tu.sofia.java.questionnaire.entities.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*; // NOPMD
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.responses.OpenResponseEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "open_questions")
@Getter
@Setter
@NoArgsConstructor
public class OpenQuestionEntity extends QuestionEntity {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OpenResponseEntity> answers = new HashSet<>();

    @Override
    public <T> void answerQuestion(final T response) throws IllegalArgumentException {
        if (response instanceof String responseText) {
            answers.add(OpenResponseEntity.builder().withQuestion(this).withResponseText(responseText).build());
        } else {
            throw new IllegalArgumentException("Invalid answer type for option question.");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final OpenQuestionEntity openQuestion;

        public Builder() {
            this.openQuestion = new OpenQuestionEntity();
        }

        public Builder withId(final Long id) {
            this.openQuestion.setId(id);
            return this;
        }

        public Builder withQuestionText(final String questionText) {
            this.openQuestion.setQuestionText(questionText);
            return this;
        }

        public Builder withQuestionnaire(final QuestionnaireEntity questionnaire) {
            this.openQuestion.setQuestionnaire(questionnaire);
            return this;
        }

        public Builder withAnswers(final Set<OpenResponseEntity> answers) {
            this.openQuestion.setAnswers(answers);
            return this;
        }

        public OpenQuestionEntity build() {
            return this.openQuestion;
        }
    }
}
