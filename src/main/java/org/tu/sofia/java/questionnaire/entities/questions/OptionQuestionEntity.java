package org.tu.sofia.java.questionnaire.entities.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*; // NOPMD
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.responses.OptionResponseEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "option_questions")
@Getter
@Setter
@NoArgsConstructor
public class OptionQuestionEntity extends QuestionEntity {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OptionResponseEntity> options = new HashSet<>();

    @Override
    public <T> void answerQuestion(final T response) throws IllegalArgumentException {
        if (response instanceof Long optionResponseEntityId) {
            // Get option by the passed ID
            final Optional<OptionResponseEntity> optionalOptionResponse = options
                    .stream()
                    .filter(optionResponseEntity ->
                            Objects.equals(optionResponseEntity.getId(), optionResponseEntityId))
                    .findFirst();
            if (optionalOptionResponse.isEmpty()) {
                throw new EntityNotFoundException("Option with this ID for this question was not found.");
            }
            // Get the Option response entity and add response to it
            optionalOptionResponse.get().addResponse();
        } else {
            throw new IllegalArgumentException("Invalid answer type for option question.");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final OptionQuestionEntity optionQuestion;

        public Builder() {
            this.optionQuestion = new OptionQuestionEntity();
        }

        public Builder withId(final Long id) {
            this.optionQuestion.setId(id);
            return this;
        }

        public Builder withQuestionText(final String questionText) {
            this.optionQuestion.setQuestionText(questionText);
            return this;
        }

        public Builder withQuestionnaire(final QuestionnaireEntity questionnaire) {
            this.optionQuestion.setQuestionnaire(questionnaire);
            return this;
        }

        public Builder withOptions(final Set<OptionResponseEntity> options) {
            this.optionQuestion.setOptions(options);
            return this;
        }

        public OptionQuestionEntity build() {
            return this.optionQuestion;
        }
    }
}
