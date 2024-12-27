package org.tu.sofia.java.questionnaire.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
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

    public QuestionnaireEntity(String title, String description, UserEntity owner, Set<UserEntity> administrators, Set<QuestionEntity> questions, Boolean isOpen, Boolean isPublic) {
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.administrators = administrators;
        this.questions = questions;
        this.isOpen = isOpen;
        this.isPublic = isPublic;
    }
}
