package org.tu.sofia.java.questionnaire.entities.responses;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import org.tu.sofia.java.questionnaire.entities.questions.OptionQuestionEntity;

@Entity
@Table(name = "option_question_responses")
@Getter
@Setter
@NoArgsConstructor
@JsonFilter("optionFilter")
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
}
