package org.tu.sofia.java.questionnaire.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*; // NOPMD

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<QuestionnaireEntity> questionnaires;

    @ManyToMany(mappedBy = "administrators")
    private Set<QuestionnaireEntity> adminQuestionnaires;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final UserEntity userEntity;

        public Builder() {
            this.userEntity = new UserEntity();
            this.userEntity.setQuestionnaires(new HashSet<>());
            this.userEntity.setAdminQuestionnaires(new HashSet<>());
        }

        public Builder withId(final Long id) {
            this.userEntity.setId(id);
            return this;
        }

        public Builder withUsername(final String username) {
            this.userEntity.setUsername(username);
            return this;
        }

        public Builder withPassword(final String password) {
            this.userEntity.setPassword(password);
            return this;
        }

        public UserEntity build() {
            return this.userEntity;
        }
    }
}
