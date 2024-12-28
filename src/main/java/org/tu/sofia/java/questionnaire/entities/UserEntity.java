package org.tu.sofia.java.questionnaire.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*; // NOPMD
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private Set<QuestionnaireEntity> adminQuestionnaires;

    public UserEntity(final Long id) {
        this.id = id;
    }

    public UserEntity(final String username, final String password) {
        this.username = username;
        this.password = password;
    }
}
