package org.tu.sofia.java.questionnaire.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tu.sofia.java.questionnaire.dtos.questions.*; // NOPMD

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class QuestionnaireDTO {
    private String title;
    private String description;
    private Boolean isOpen;
    private Boolean isPublic;
    private Set<BooleanQuestionDTO> booleanQuestionDTOSet;
    private Set<OpenQuestionDTO> openQuestionDTOSet;
    private Set<OptionQuestionDTO> optionQuestionDTOSet;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final QuestionnaireDTO questionnaire;

        public Builder() {
            this.questionnaire = new QuestionnaireDTO();
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

        public Builder withBooleanQuestionDTOSet(final Set<BooleanQuestionDTO> booleanQuestionDTOSet) {
            this.questionnaire.setBooleanQuestionDTOSet(booleanQuestionDTOSet);
            return this;
        }

        public Builder withOpenQuestionDTOSet(final Set<OpenQuestionDTO> openQuestionDTOSet) {
            this.questionnaire.setOpenQuestionDTOSet(openQuestionDTOSet);
            return this;
        }

        public Builder withOptionQuestionDTOSet(final Set<OptionQuestionDTO> optionQuestionDTOSet) {
            this.questionnaire.setOptionQuestionDTOSet(optionQuestionDTOSet);
            return this;
        }

        public QuestionnaireDTO build() {
            return this.questionnaire;
        }
    }
}
