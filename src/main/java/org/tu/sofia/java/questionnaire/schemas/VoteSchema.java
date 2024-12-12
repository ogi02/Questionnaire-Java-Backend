package org.tu.sofia.java.questionnaire.schemas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VoteSchema {

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Vote {
        private Long questionId;
        private Long optionId;
    }

    Set<Vote> votes;
}
