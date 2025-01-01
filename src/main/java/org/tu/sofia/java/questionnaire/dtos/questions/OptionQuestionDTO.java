package org.tu.sofia.java.questionnaire.dtos.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dtos.responses.OptionResponseDTO;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OptionQuestionDTO extends QuestionDTO {
    private Set<OptionResponseDTO> optionResponseDTOSet;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OptionQuestionDTO optionQuestion;

        public Builder() {
            this.optionQuestion = new OptionQuestionDTO();
        }

        public Builder withQuestionText(final String questionText) {
            this.optionQuestion.setQuestionText(questionText);
            return this;
        }

        public Builder withOptionResponseDTOSet(final Set<OptionResponseDTO> optionResponseDTOSet) {
            this.optionQuestion.setOptionResponseDTOSet(optionResponseDTOSet);
            return this;
        }

        public OptionQuestionDTO build() {
            return this.optionQuestion;
        }
    }
}
