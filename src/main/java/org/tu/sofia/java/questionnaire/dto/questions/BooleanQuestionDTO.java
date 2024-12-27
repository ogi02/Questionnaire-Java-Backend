package org.tu.sofia.java.questionnaire.dto.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BooleanQuestionDTO extends QuestionDTO {
    public BooleanQuestionDTO(String questionText) {
        super(questionText);
    }
}
