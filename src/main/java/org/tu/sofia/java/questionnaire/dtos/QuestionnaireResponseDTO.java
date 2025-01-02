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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final QuestionnaireResponseDTO dto;

        public Builder() {
            this.dto = new QuestionnaireResponseDTO();
        }

        public Builder withAnswers(final Map<Long, Object> answers) {
            this.dto.setAnswers(answers);
            return this;
        }

        public QuestionnaireResponseDTO build() {
            return this.dto;
        }
    }
}
