package org.tu.sofia.java.questionnaire.entities.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
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

    public OpenResponseEntity(OpenQuestionEntity question, String responseText) {
        this.question = question;
        this.responseText = responseText;
    }
}
