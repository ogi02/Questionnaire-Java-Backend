package org.tu.sofia.java.questionnaire.entities.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import org.tu.sofia.java.questionnaire.entities.responses.OpenResponseEntity;

import java.util.Set;

@Entity
@Table(name = "open_questions")
@Getter
@Setter
@NoArgsConstructor
public class OpenQuestionEntity extends QuestionEntity {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OpenResponseEntity> answers;

    @Override
    public <T> void answerQuestion(T response) {
        if (response instanceof String responseText) {
            answers.add(new OpenResponseEntity(this, responseText));
        }
    }
}
