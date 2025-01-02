package org.tu.sofia.java.questionnaire.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final UserDTO user;

        public Builder() {
            this.user = new UserDTO();
        }

        public Builder withId(final Long id) {
            this.user.setId(id);
            return this;
        }

        public Builder withUsername(final String username) {
            this.user.setUsername(username);
            return this;
        }

        public UserDTO build() {
            return this.user;
        }
    }
}
