package org.tu.sofia.java.questionnaire.dto.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BooleanQuestionCreationDTO extends QuestionDTO {
    public BooleanQuestionCreationDTO(String questionText) {
        super(questionText);
    }
}
