package org.tu.sofia.java.questionnaire.entities.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import org.tu.sofia.java.questionnaire.entities.responses.OptionResponseEntity;

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
    private Set<OptionResponseEntity> options;

    @Override
    public <T> void answerQuestion(T response) throws IllegalArgumentException {
        if (response instanceof Integer optionResponseEntityId) {
            // Get option by the passed ID
            Optional<OptionResponseEntity> optionalOptionResponseEntity = options.stream().filter(optionResponseEntity -> Objects.equals(optionResponseEntity.getId(), optionResponseEntityId.longValue())).findFirst();
            if (optionalOptionResponseEntity.isEmpty()) {
                throw new EntityNotFoundException("Option with this ID for this question was not found.");
            }
            // Get the Option response entity and add response to it
            optionalOptionResponseEntity.get().addResponse();
        } else {
            throw new IllegalArgumentException("Invalid response type for option question!");
        }
    }
}
