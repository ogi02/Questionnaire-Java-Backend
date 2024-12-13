package org.tu.sofia.java.questionnaire.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<QuestionnaireEntity> questionnaires;

    @ManyToMany(mappedBy = "administrators")
    private Set<QuestionnaireEntity> administratedQuestionnaires;

    public UserEntity(Long id) {
        this.id = id;
    }

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserEntity(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
