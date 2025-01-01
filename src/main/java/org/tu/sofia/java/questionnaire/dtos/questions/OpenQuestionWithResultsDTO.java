package org.tu.sofia.java.questionnaire.dtos.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dtos.responses.OpenResponseWithResultsDTO;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OpenQuestionWithResultsDTO extends QuestionDTO {
    private Set<OpenResponseWithResultsDTO> openResponseWithResultsDTOSet;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OpenQuestionWithResultsDTO openQuestionWithResults;

        public Builder() {
            this.openQuestionWithResults = new OpenQuestionWithResultsDTO();
        }

        public Builder withQuestionText(final String questionText) {
            this.openQuestionWithResults.setQuestionText(questionText);
            return this;
        }

        public Builder withOpenResponseWithResultsDTOSet(final Set<OpenResponseWithResultsDTO> set) {
            this.openQuestionWithResults.setOpenResponseWithResultsDTOSet(set);
            return this;
        }

        public OpenQuestionWithResultsDTO build() {
            return this.openQuestionWithResults;
        }
    }
}
