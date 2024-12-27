package org.tu.sofia.java.questionnaire.dto.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dto.responses.OpenResponseDTO;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OpenQuestionWithResultsDTO extends QuestionDTO {
    private Set<OpenResponseDTO> openResponseDTOSet;

    public OpenQuestionWithResultsDTO(String questionText) {
        super(questionText);
        this.openResponseDTOSet = new HashSet<>();
    }

    public OpenQuestionWithResultsDTO(String questionText, Set<OpenResponseDTO> openResponseDTOSet) {
        super(questionText);
        this.openResponseDTOSet = openResponseDTOSet;
    }
}
