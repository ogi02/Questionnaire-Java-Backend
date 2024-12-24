package org.tu.sofia.java.questionnaire.entities;

import com.fasterxml.jackson.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import org.tu.sofia.java.questionnaire.entities.questions.OptionQuestionEntity;
import org.tu.sofia.java.questionnaire.entities.questions.QuestionEntity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "questionnaires")
@Getter
@Setter
@NoArgsConstructor
@JsonFilter("questionnaireFilter")
public class QuestionnaireEntity {
    @Id
    @Hidden
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questionnaire_generator")
    @SequenceGenerator(name = "questionnaire_generator", sequenceName = "questionnaire_seq", allocationSize = 1)
    private Long id;

    @Column
    @JsonProperty("title")
    private String title;

    @Column
    @JsonProperty("description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @JsonIgnore
    private UserEntity owner;

    @ManyToMany
    @JoinTable(
            name = "questionnaire_administrators",
            joinColumns = @JoinColumn(name = "questionnaire_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<UserEntity> administrators = new HashSet<>();

    @OneToMany(mappedBy = "questionnaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionEntity> questions;

    @Column
    @JsonProperty("isOpen")
    private Boolean isOpen;

    @Column
    @JsonProperty("isPublic")
    private Boolean isPublic;

    @Column
    @Hidden
    private String votingUrl = UUID.randomUUID().toString().replace("-", "");

    @Column
    @Hidden
    private String resultsUrl = UUID.randomUUID().toString().replace("-", "");

    public QuestionnaireEntity(String title, Boolean isOpen, Boolean isPublic) {
        this.title = title;
        this.isOpen = isOpen;
        this.isPublic = isPublic;
    }

    public QuestionnaireEntity(String title, String votingUrl, Boolean isOpen) {
        this.title = title;
        this.votingUrl = votingUrl;
        this.isOpen = isOpen;
    }

    public QuestionnaireEntity(String title) {
        this.title = title;
    }

//    public OptionQuestionEntity findQuestionById(Long id) throws EntityNotFoundException {
//        Optional<OptionQuestionEntity> optionalQuestion = questions.stream().filter(question -> question.getId().equals(id)).findFirst();
//        if (optionalQuestion.isEmpty()) {
//            throw new EntityNotFoundException("Question with this ID not found!");
//        }
//
//        return optionalQuestion.get();
//    }
}
