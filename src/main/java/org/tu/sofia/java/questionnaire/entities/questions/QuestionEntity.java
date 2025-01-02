package org.tu.sofia.java.questionnaire.entities.questions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*; // NOPMD
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "questions")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_generator")
    @SequenceGenerator(name = "question_generator", sequenceName = "question_seq", allocationSize = 1)
    private Long id;

    @Column
    private String questionText;

    @ManyToOne
    @JoinColumn(name = "questionnaire_id", referencedColumnName = "id")
    private QuestionnaireEntity questionnaire;

    public abstract <T> void answerQuestion(T response) throws IllegalArgumentException;
}
