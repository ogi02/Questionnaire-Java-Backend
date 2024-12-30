package org.tu.sofia.java.questionnaire.dtos.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dtos.responses.OpenResponseWithResultsDTO;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OpenQuestionWithResultsDTO extends QuestionDTO {
    private Set<OpenResponseWithResultsDTO> openResponseWithResultsDTOSet;

    public OpenQuestionWithResultsDTO(final String questionText) {
        super(questionText);
        this.openResponseWithResultsDTOSet = new HashSet<>();
    }

    public OpenQuestionWithResultsDTO(final String questionText,
                                      final Set<OpenResponseWithResultsDTO> openResponseWithResultsDTOSet) {
        super(questionText);
        this.openResponseWithResultsDTOSet = openResponseWithResultsDTOSet;
    }
}
