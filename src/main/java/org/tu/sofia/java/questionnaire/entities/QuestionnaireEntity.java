package org.tu.sofia.java.questionnaire.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*; // NOPMD
import org.tu.sofia.java.questionnaire.entities.questions.QuestionEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "questionnaires")
@Getter
@Setter
@NoArgsConstructor
public class QuestionnaireEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questionnaire_generator")
    @SequenceGenerator(name = "questionnaire_generator", sequenceName = "questionnaire_seq", allocationSize = 1)
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private UserEntity owner;

    @ManyToMany
    @JoinTable(
            name = "questionnaire_administrators",
            joinColumns = @JoinColumn(name = "questionnaire_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> administrators = new HashSet<>();

    @OneToMany(mappedBy = "questionnaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionEntity> questions;

    @Column(name = "is_open")
    private Boolean isOpen;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "answer_url")
    private String answerURL = UUID.randomUUID().toString().replace("-", "");

    @Column(name = "results_url")
    private String resultsURL = UUID.randomUUID().toString().replace("-", "");

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final QuestionnaireEntity questionnaire;

        public Builder() {
            this.questionnaire = new QuestionnaireEntity();
        }

        public Builder withId(final Long id) {
            this.questionnaire.setId(id);
            return this;
        }

        public Builder withTitle(final String title) {
            this.questionnaire.setTitle(title);
            return this;
        }

        public Builder withDescription(final String description) {
            this.questionnaire.setDescription(description);
            return this;
        }

        public Builder withOwner(final UserEntity owner) {
            this.questionnaire.setOwner(owner);
            return this;
        }

        public Builder withAdministrators(final Set<UserEntity> administrators) {
            this.questionnaire.setAdministrators(administrators);
            return this;
        }

        public Builder withQuestions(final Set<QuestionEntity> questions) {
            this.questionnaire.setQuestions(questions);
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

        public QuestionnaireEntity build() {
            return this.questionnaire;
        }
    }
}
