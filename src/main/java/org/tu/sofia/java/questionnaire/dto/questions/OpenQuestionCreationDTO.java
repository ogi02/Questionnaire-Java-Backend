package org.tu.sofia.java.questionnaire.dto.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpenQuestionCreationDTO extends QuestionDTO {
    public OpenQuestionCreationDTO(String questionText) {
        super(questionText);
    }
}
