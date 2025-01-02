package org.tu.sofia.java.questionnaire.dtos.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OptionResponseDTO {
    private String option;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OptionResponseDTO dto;

        public Builder() {
            this.dto = new OptionResponseDTO();
        }

        public Builder withOption(final String option) {
            this.dto.setOption(option);
            return this;
        }

        public OptionResponseDTO build() {
            return this.dto;
        }
    }
}
