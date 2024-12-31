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

    public OpenQuestionEntity(final String questionText) {
        super(questionText);
    }

    public OpenQuestionEntity(final Long id, final String questionText, final QuestionnaireEntity questionnaire) {
        super(id, questionText, questionnaire);
    }

    public OpenQuestionEntity(final Long id, final String questionText, final QuestionnaireEntity questionnaire,
                              final Set<OpenResponseEntity> answers) {
        super(id, questionText, questionnaire);
        this.answers = answers;
    }

    @Override
    public <T> void answerQuestion(final T response) throws IllegalArgumentException {
        if (response instanceof String responseText) {
            answers.add(new OpenResponseEntity(this, responseText));
        } else {
            throw new IllegalArgumentException("Invalid answer type for option question.");
        }
    }
}
