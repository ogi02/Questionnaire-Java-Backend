package org.tu.sofia.java.questionnaire;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.repositories.QuestionnaireRepository;
import org.tu.sofia.java.questionnaire.schemas.VoteSchema;
import org.tu.sofia.java.questionnaire.services.QuestionnaireService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = QuestionnaireApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class QuestionnaireApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuestionnaireRepository questionnaireRepositoryMock;

    private QuestionnaireService questionnaireService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        questionnaireService = new QuestionnaireService(questionnaireRepositoryMock);
    }

    @Test
    void findForm() throws EntityNotFoundException {
        Mockito.lenient().when(questionnaireRepositoryMock.findById(3L)).thenReturn(Optional.of(new QuestionnaireEntity("Title")));
        QuestionnaireEntity questionnaireEntity = questionnaireService.findByQuestionnaireId(3L);
        assert questionnaireEntity != null;
        assert questionnaireEntity.getTitle().equals("Title");
    }

    @Test
    void findFormOpenedPublic() {
        QuestionnaireEntity questionnaire = new QuestionnaireEntity("Title", true, true);
        Set<QuestionnaireEntity> questionnaireEntities = new HashSet<>();
        questionnaireEntities.add(questionnaire);
        Mockito.lenient().when(questionnaireRepositoryMock.findPublic()).thenReturn(Optional.of(questionnaireEntities));
        Set<QuestionnaireEntity> testQuestionnaireEntities = questionnaireService.findPublic();
        assert testQuestionnaireEntities.size() == 1;
    }

    @Test
    void findFormClosedPublic() {
        Set<QuestionnaireEntity> questionnaireEntities = new HashSet<>();
        Mockito.lenient().when(questionnaireRepositoryMock.findPublic()).thenReturn(Optional.of(questionnaireEntities));
        Set<QuestionnaireEntity> testQuestionnaireEntities = questionnaireService.findPublic();
        assert testQuestionnaireEntities.isEmpty();
    }

    @Test
    void voteFail() throws Exception {
        String votingUrl = UUID.randomUUID().toString().replace("-", "");
        // init votes
        VoteSchema.Vote vote = new VoteSchema.Vote(1L, 2L);
        Set<VoteSchema.Vote> votes = new HashSet<>(List.of(vote));
        VoteSchema schema = new VoteSchema(votes);

        Mockito.lenient().when(questionnaireRepositoryMock.findByVotingUrl(votingUrl)).thenReturn(Optional.of(new QuestionnaireEntity("Title", votingUrl, false)));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/questionnaire/vote".concat(votingUrl))
                .content(objectMapper.writeValueAsString(schema)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(401));
    }
}
