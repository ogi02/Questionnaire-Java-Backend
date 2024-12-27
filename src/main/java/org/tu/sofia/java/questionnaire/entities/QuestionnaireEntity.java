package org.tu.sofia.java.questionnaire.entities;

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

    @Column
    private Boolean isOpen;

    @Column
    private Boolean isPublic;

    @Column
    private String answerURL = UUID.randomUUID().toString().replace("-", "");

    @Column
    private String resultsURL = UUID.randomUUID().toString().replace("-", "");

    public QuestionnaireEntity(String title, Boolean isOpen, Boolean isPublic) {
        this.title = title;
        this.isOpen = isOpen;
        this.isPublic = isPublic;
    }

    public QuestionnaireEntity(String title, String answerURL, Boolean isOpen) {
        this.title = title;
        this.answerURL = answerURL;
        this.isOpen = isOpen;
    }

    public QuestionnaireEntity(String title) {
        this.title = title;
    }
}
