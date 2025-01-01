package org.tu.sofia.java.questionnaire.dtos.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OpenResponseWithResultsDTO {
    private String responseText;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OpenResponseWithResultsDTO dto;

        public Builder() {
            this.dto = new OpenResponseWithResultsDTO();
        }

        public Builder withResponseText(final String responseText) {
            this.dto.setResponseText(responseText);
            return this;
        }

        public OpenResponseWithResultsDTO build() {
            return this.dto;
        }
    }
}
