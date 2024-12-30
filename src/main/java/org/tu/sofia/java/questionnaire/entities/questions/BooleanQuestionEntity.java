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

    public BooleanQuestionEntity(final String questionText) {
        super(questionText);
    }

    public BooleanQuestionEntity(final Long id, final String questionText, final QuestionnaireEntity questionnaire) {
        super(id, questionText, questionnaire);
    }

    public BooleanQuestionEntity(final Long id, final String questionText, final QuestionnaireEntity questionnaire,
                                 final Integer trueVotes, final Integer falseVotes) {
        super(id, questionText, questionnaire);
        this.trueVotes = trueVotes;
        this.falseVotes = falseVotes;
    }

    @Override
    public <T> void answerQuestion(final T response) throws IllegalArgumentException {
        if (response instanceof Boolean answer) {
            if (answer) {
                trueVotes++;
            } else {
                falseVotes++;
            }
        } else {
            throw new IllegalArgumentException("Invalid response type for boolean question!");
        }
    }
}
