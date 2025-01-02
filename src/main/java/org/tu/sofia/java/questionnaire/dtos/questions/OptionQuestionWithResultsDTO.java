package org.tu.sofia.java.questionnaire.dtos.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseWithResultsDTO;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OptionQuestionWithResultsDTO extends QuestionDTO {
    private Set<OptionResponseWithResultsDTO> optionResponseWithResultsDTOSet;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OptionQuestionWithResultsDTO optionQuestionWithResults;

        public Builder() {
            this.optionQuestionWithResults = new OptionQuestionWithResultsDTO();
        }

        public Builder withQuestionText(final String questionText) {
            this.optionQuestionWithResults.setQuestionText(questionText);
            return this;
        }

        public Builder withOptionResponseWithResultsDTOSet(final Set<OptionResponseWithResultsDTO> dtoSet) {
            this.optionQuestionWithResults.setOptionResponseWithResultsDTOSet(dtoSet);
            return this;
        }

        public OptionQuestionWithResultsDTO build() {
            return this.optionQuestionWithResults;
        }
    }
}
