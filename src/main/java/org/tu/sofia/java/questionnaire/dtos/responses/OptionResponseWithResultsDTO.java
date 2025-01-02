package org.tu.sofia.java.questionnaire.dtos.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OptionResponseWithResultsDTO {
    private String option;
    private Integer votes = 0;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OptionResponseWithResultsDTO dto;

        public Builder() {
            this.dto = new OptionResponseWithResultsDTO();
        }

        public Builder withOption(final String option) {
            this.dto.setOption(option);
            return this;
        }

        public Builder withVotes(final Integer votes) {
            this.dto.setVotes(votes);
            return this;
        }

        public OptionResponseWithResultsDTO build() {
            return this.dto;
        }
    }
}
