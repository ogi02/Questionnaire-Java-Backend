package org.tu.sofia.java.questionnaire.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OptionResponseDTO {
    private String option;
    private Integer votes = 0;
}
