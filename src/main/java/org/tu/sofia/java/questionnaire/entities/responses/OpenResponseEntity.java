package org.tu.sofia.java.questionnaire.entities.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*; // NOPMD
import org.tu.sofia.java.questionnaire.entities.questions.OpenQuestionEntity;

@Entity
@Table(name = "open_question_responses")
@Getter
@Setter
@NoArgsConstructor
public class OpenResponseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "option_generator")
    @SequenceGenerator(name = "option_generator", sequenceName = "option_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private OpenQuestionEntity question;

    @Column
    private String responseText;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OpenResponseEntity response;

        public Builder() {
            response = new OpenResponseEntity();
        }

        public Builder withId(final Long id) {
            this.response.setId(id);
            return this;
        }

        public Builder withQuestion(final OpenQuestionEntity question) {
            this.response.setQuestion(question);
            return this;
        }

        public Builder withResponseText(final String responseText) {
            this.response.setResponseText(responseText);
            return this;
        }

        public OpenResponseEntity build() {
            return this.response;
        }
    }
}
