package org.tu.sofia.java.questionnaire.entities.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import org.tu.sofia.java.questionnaire.entities.responses.OptionResponseEntity;

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

    public OptionResponseEntity findOptionById(Long id) throws EntityNotFoundException {
        Optional<OptionResponseEntity> optionalOption = options.stream().filter(option -> option.getId().equals(id)).findFirst();
        if (optionalOption.isEmpty()) {
            throw new EntityNotFoundException("Option with this ID not found");
        }

        return optionalOption.get();
    }

    @Override
    public <T> void answerQuestion(T response) {
        if (response instanceof OptionResponseEntity optionResponseEntity) {
            optionResponseEntity.addResponse();
        }
    }
}
