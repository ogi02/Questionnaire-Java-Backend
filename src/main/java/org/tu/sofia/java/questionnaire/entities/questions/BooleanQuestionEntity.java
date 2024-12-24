package org.tu.sofia.java.questionnaire.entities.questions;

import io.swagger.v3.oas.annotations.Hidden;
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
    @Hidden
    private Integer trueVotes = 0;

    @Column
    @Hidden
    private Integer falseVotes = 0;

    @Override
    public <T> void answerQuestion(T response) {
        if (response instanceof Boolean b) {
            if (b) {
                trueVotes++;
            } else {
                falseVotes++;
            }
        }
    }
}
