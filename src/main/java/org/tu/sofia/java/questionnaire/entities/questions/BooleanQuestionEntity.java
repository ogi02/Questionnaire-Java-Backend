package org.tu.sofia.java.questionnaire.entities.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

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
    public <T> void answerQuestion(T response) throws IllegalArgumentException {
        if (response instanceof Boolean b) {
            if (b) {
                trueVotes++;
            } else {
                falseVotes++;
            }
        } else {
            throw new IllegalArgumentException("Invalid response type for boolean question!");
        }
    }
}
