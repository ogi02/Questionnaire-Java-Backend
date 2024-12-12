package org.tu.sofia.java.questionnaire.schemas;

import java.io.Serial;
import java.io.Serializable;

public record DefaultErrorResponseSchema(String timestamp, int status, String error, String path) implements Serializable {

    @Serial
    private static final long serialVersionUID = 7990328244355345972L;
}
