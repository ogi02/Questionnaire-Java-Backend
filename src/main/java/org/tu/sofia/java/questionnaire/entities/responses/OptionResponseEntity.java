package org.tu.sofia.java.questionnaire.entities.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*; // NOPMD
import org.tu.sofia.java.questionnaire.entities.questions.OptionQuestionEntity;

@Entity
@Table(name = "option_question_responses")
@Getter
@Setter
@NoArgsConstructor
public class OptionResponseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "option_generator")
    @SequenceGenerator(name = "option_generator", sequenceName = "option_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private OptionQuestionEntity question;

    @Column
    private String option;

    @Column
    private Integer votes = 0;

    public void addResponse() {
        this.votes += 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final OptionResponseEntity response;

        public Builder() {
            response = new OptionResponseEntity();
        }

        public Builder withId(final Long id) {
            this.response.setId(id);
            return this;
        }

        public Builder withQuestion(final OptionQuestionEntity question) {
            this.response.setQuestion(question);
            return this;
        }

        public Builder withOption(final String option) {
            this.response.setOption(option);
            return this;
        }

        public Builder withVotes(final Integer votes) {
            this.response.setVotes(votes);
            return this;
        }

        public OptionResponseEntity build() {
            return this.response;
        }
    }
}
