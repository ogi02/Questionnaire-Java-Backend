package org.tu.sofia.java.questionnaire.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
public class QuestionEntity {
    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_generator")
    @SequenceGenerator(name = "question_generator", sequenceName = "question_seq", allocationSize = 1)
    private Long id;

    @Column
    private String question;

    @ManyToOne
    @JoinColumn(name = "questionnaire_id", referencedColumnName = "id")
    @JsonIgnore
    private QuestionnaireEntity questionnaire;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private Set<OptionEntity> options;

    public OptionEntity findOptionById(Long id) throws EntityNotFoundException {
        Optional<OptionEntity> optionalOption = options.stream().filter(option -> option.getId().equals(id)).findFirst();
        if (optionalOption.isEmpty()) {
            throw new EntityNotFoundException("Option with this ID not found");
        }

        return optionalOption.get();
    }
}
