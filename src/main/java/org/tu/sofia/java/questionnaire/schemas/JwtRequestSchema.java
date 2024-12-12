package org.tu.sofia.java.questionnaire.schemas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequestSchema implements Serializable {

    @Serial
    private static final long serialVersionUID = 5926468583005150707L;

    private String username;
    private String password;
}
