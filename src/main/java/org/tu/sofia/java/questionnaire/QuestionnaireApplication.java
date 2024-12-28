package org.tu.sofia.java.questionnaire;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionnaireApplication {

    public static void main(final String[] args) {
        SpringApplication.run(QuestionnaireApplication.class, args);
    }

}
