package org.tu.sofia.java.questionnaire.dto.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpenQuestionDTO extends QuestionDTO {
    public OpenQuestionDTO(String questionText) {
        super(questionText);
    }
}
