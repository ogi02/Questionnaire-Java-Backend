package org.tu.sofia.java.questionnaire.dtos.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BooleanQuestionDTO extends QuestionDTO {
    public BooleanQuestionDTO(final String questionText) {
        super(questionText);
    }
}
