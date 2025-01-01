package org.tu.sofia.java.questionnaire.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dtos.questions.*; // NOPMD

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class QuestionnaireWithResultsDTO {
    private String title;
    private String description;
    private Boolean isOpen;
    private Boolean isPublic;
    private Set<BooleanQuestionWithResultsDTO> booleanQuestionsResults;
    private Set<OpenQuestionWithResultsDTO> openQuestionsResults;
    private Set<OptionQuestionWithResultsDTO> optionQuestionsResults;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final QuestionnaireWithResultsDTO questionnaire;

        public Builder() {
            this.questionnaire = new QuestionnaireWithResultsDTO();
        }

        public Builder withTitle(final String title) {
            this.questionnaire.setTitle(title);
            return this;
        }

        public Builder withDescription(final String description) {
            this.questionnaire.setDescription(description);
            return this;
        }

        public Builder withIsOpen(final Boolean isOpen) {
            this.questionnaire.setIsOpen(isOpen);
            return this;
        }

        public Builder withIsPublic(final Boolean isPublic) {
            this.questionnaire.setIsPublic(isPublic);
            return this;
        }

        public Builder withBooleanQuestionDTOSet(final Set<BooleanQuestionWithResultsDTO> booleanQuestionDTOSet) {
            this.questionnaire.setBooleanQuestionsResults(booleanQuestionDTOSet);
            return this;
        }

        public Builder withOpenQuestionDTOSet(final Set<OpenQuestionWithResultsDTO> openQuestionDTOSet) {
            this.questionnaire.setOpenQuestionsResults(openQuestionDTOSet);
            return this;
        }

        public Builder withOptionQuestionDTOSet(final Set<OptionQuestionWithResultsDTO> optionQuestionDTOSet) {
            this.questionnaire.setOptionQuestionsResults(optionQuestionDTOSet);
            return this;
        }

        public QuestionnaireWithResultsDTO build() {
            return this.questionnaire;
        }
    }
}
