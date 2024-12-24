package org.tu.sofia.java.questionnaire.schemas;

import java.io.Serial;
import java.io.Serializable;

public record JwtResponseSchema(String token, String username) implements Serializable {

    @Serial
    private static final long serialVersionUID = -8091879091924046844L;
}
