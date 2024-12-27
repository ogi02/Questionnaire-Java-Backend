package org.tu.sofia.java.questionnaire.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class QuestionnaireResponseDTO {
    private Map<Long, Object> answers;
}