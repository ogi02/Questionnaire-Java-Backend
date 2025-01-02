package org.tu.sofia.java.questionnaire.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.*; // NOPMD
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.tu.sofia.java.questionnaire.config.JwtRequestFilter;
import org.tu.sofia.java.questionnaire.controllers.QuestionnaireController;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireDTO;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireResponseDTO;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireWithResultsDTO;
import org.tu.sofia.java.questionnaire.services.QuestionnaireService;
import org.tu.sofia.java.questionnaire.unit.utilities.QuestionnaireCreator;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*; // NOPMD
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // NOPMD

@WebMvcTest(QuestionnaireController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ComponentScan("org.tu.sofia.java.questionnaire.unit.utilities")
@NoArgsConstructor
public class QuestionnaireControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtRequestFilter jwtRequestFilter;

    @MockitoBean
    private QuestionnaireService questionnaireService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private TestingAuthenticationToken token;

    @Value("${unit.test.username}")
    private String testUsername;

    @Value("${unit.test.password}")
    private String testPassword;

    @Value("${unit.test.questionnaire.title}")
    protected String title;

    @Value("${unit.test.questionnaire.description}")
    protected String description;

    @Value("${unit.test.questionnaire.isOpen}")
    protected Boolean isOpen;

    @Value("${unit.test.questionnaire.isPublic}")
    protected Boolean isPublic;

    @BeforeAll
    public void updateToken() {
        final User user = new User(testUsername, testPassword, new HashSet<>());
        this.token = new TestingAuthenticationToken(user, null);
    }

    @Nested
    @NoArgsConstructor
    public class CreateQuestionnaire {
        @Test
        public void success() throws Exception {
            // Init a questionnaire for a body of the "createQuestionnaire" request
            final QuestionnaireDTO bodyQ = QuestionnaireCreator.createDefaultDTO().build();
            // Init a questionnaire for the "createQuestionnaire" return value
            final QuestionnaireWithResultsDTO returnQ = QuestionnaireCreator.createDefaultDTOWithResults().build();

            // Mock the "createQuestionnaire" method of the questionnaire service
            doReturn(returnQ)
                    .when(questionnaireService).createQuestionnaire(eq(testUsername), any(QuestionnaireDTO.class));

            // Perform POST request to /api/questionnaire
            mockMvc.perform(post("/api/questionnaire/")
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bodyQ)))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"title\":\"%s\"".formatted(title))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"description\":\"%s\"".formatted(description))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"isOpen\":%s".formatted(isOpen))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"isPublic\":%s".formatted(isPublic))));
        }
    }

    @Nested
    @NoArgsConstructor
    public class DeleteQuestionnaire {
        final static private Long ID = 1L;

        @Test
        public void success() throws Exception {
            // Mock the "deleteQuestionnaire" method of the questionnaire service
            doNothing().when(questionnaireService).deleteQuestionnaire(testUsername, ID);

            // Perform DELETE request to /api/questionnaire/{id}
            mockMvc.perform(delete("/api/questionnaire/{id}", ID)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());
        }

        @Test
        public void notAdministrator() throws Exception {
            // Mock the "deleteQuestionnaire" method of the questionnaire service
            doThrow(IllegalAccessException.class)
                    .when(questionnaireService).deleteQuestionnaire(testUsername, ID);

            // Perform DELETE request to /api/questionnaire/{id}
            mockMvc.perform(delete("/api/questionnaire/{id}", ID)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        public void notFound() throws Exception {
            // Mock the "deleteQuestionnaire" method of the questionnaire service
            doThrow(EntityNotFoundException.class)
                    .when(questionnaireService).deleteQuestionnaire(testUsername, ID);

            // Perform DELETE request to /api/questionnaire/{id}
            mockMvc.perform(delete("/api/questionnaire/{id}", ID)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @NoArgsConstructor
    public class GetPublicQuestionnaires {
        @Test
        public void successPresent() throws Exception {
            // Init a questionnaire for the "getPublicQuestionnaires" return value
            final QuestionnaireWithResultsDTO returnQ = QuestionnaireCreator.createDefaultDTOWithResults().build();

            // Mock the "getPublicQuestionnaires" method of the questionnaire service
            doReturn(Set.of(returnQ)).when(questionnaireService).getPublicQuestionnaires();

            // Perform GET request to /api/questionnaire/public
            mockMvc.perform(get("/api/questionnaire/public")
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"title\":\"%s\"".formatted(title))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"description\":\"%s\"".formatted(description))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"isOpen\":%s".formatted(isOpen))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"isPublic\":%s".formatted(isPublic))));
        }

        @Test
        public void successEmpty() throws Exception {
            // Mock the "getPublicQuestionnaires" method of the questionnaire service
            doReturn(new HashSet<>()).when(questionnaireService).getPublicQuestionnaires();

            // Perform GET request to /api/questionnaire/public
            mockMvc.perform(get("/api/questionnaire/public")
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());
        }
    }

    @Nested
    @NoArgsConstructor
    public class GetUserQuestionnaires {
        @Test
        public void successPresent() throws Exception {
            // Init a questionnaire for the "getUserAdministratedQuestionnaires" return value
            final QuestionnaireWithResultsDTO returnQ = QuestionnaireCreator.createDefaultDTOWithResults().build();

            // Mock the "getUserAdministratedQuestionnaires" method of the questionnaire service
            doReturn(Set.of(returnQ)).when(questionnaireService).getUserAdministratedQuestionnaires(testUsername);

            // Perform GET request to /api/questionnaire/user
            mockMvc.perform(get("/api/questionnaire/user")
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"title\":\"%s\"".formatted(title))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"description\":\"%s\"".formatted(description))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"isOpen\":%s".formatted(isOpen))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"isPublic\":%s".formatted(isPublic))));
        }

        @Test
        public void successEmpty() throws Exception {
            // Mock the "getUserAdministratedQuestionnaires" method of the questionnaire service
            doReturn(new HashSet<>()).when(questionnaireService).getUserAdministratedQuestionnaires(testUsername);

            // Perform GET request to /api/questionnaire/user
            mockMvc.perform(get("/api/questionnaire/user")
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());
        }
    }

    @Nested
    @NoArgsConstructor
    public class UpdateQuestionnaireState {
        final static private Long ID = 1L;
        final static private Boolean IS_OPEN = true;

        @Test
        public void success() throws Exception {
            // Mock the "updateQuestionnaireState" method of the questionnaire service
            doNothing().when(questionnaireService).updateQuestionnaireState(testUsername, ID, IS_OPEN);

            // Perform PUT request to /api/questionnaire/{id}/status/{isOpen}
            mockMvc.perform(put("/api/questionnaire/{id}/state/{isOpen}", ID, IS_OPEN)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());
        }

        @Test
        public void notAdministrator() throws Exception {
            // Mock the "updateQuestionnaireState" method of the questionnaire service
            doThrow(IllegalAccessException.class)
                    .when(questionnaireService).updateQuestionnaireState(testUsername, ID, IS_OPEN);

            // Perform PUT request to /api/questionnaire/{id}/status/{isOpen}
            mockMvc.perform(put("/api/questionnaire/{id}/state/{isOpen}", ID, IS_OPEN)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        public void notFound() throws Exception {
            // Mock the "updateQuestionnaireState" method of the questionnaire service
            doThrow(EntityNotFoundException.class)
                    .when(questionnaireService).updateQuestionnaireState(testUsername, ID, IS_OPEN);

            // Perform PUT request to /api/questionnaire/{id}/status/{isOpen}
            mockMvc.perform(put("/api/questionnaire/{id}/state/{isOpen}", ID, IS_OPEN)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @NoArgsConstructor
    public class AddAdministratorToQuestionnaire {
        final static private Long ID = 1L;
        final static private Long USER_ID = 2L;

        @Test
        public void success() throws Exception {
            // Mock the "addAdministratorToQuestionnaire" method of the questionnaire service
            doNothing().when(questionnaireService).addAdministratorToQuestionnaire(testUsername, ID, USER_ID);

            // Perform POST request to /api/questionnaire/{id}/admin/{userId}
            mockMvc.perform(post("/api/questionnaire/{id}/admin/{userId}", ID, USER_ID)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());
        }

        @Test
        public void notAdministrator() throws Exception {
            // Mock the "addAdministratorToQuestionnaire" method of the questionnaire service
            doThrow(IllegalAccessException.class)
                    .when(questionnaireService).addAdministratorToQuestionnaire(testUsername, ID, USER_ID);

            // Perform POST request to /api/questionnaire/{id}/admin/{userId}
            mockMvc.perform(post("/api/questionnaire/{id}/admin/{userId}", ID, USER_ID)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        public void notFound() throws Exception {
            // Mock the "addAdministratorToQuestionnaire" method of the questionnaire service
            doThrow(EntityNotFoundException.class)
                    .when(questionnaireService).addAdministratorToQuestionnaire(testUsername, ID, USER_ID);

            // Perform POST request to /api/questionnaire/{id}/admin/{userId}
            mockMvc.perform(post("/api/questionnaire/{id}/admin/{userId}", ID, USER_ID)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @NoArgsConstructor
    public class GetQuestionnaireByAnswerURL {
        final static private String ANSWER_URL = "testAnswerURL";

        @Test
        public void success() throws Exception {
            // Init a questionnaire for the "getQuestionnaireByAnswerURL" return value
            final QuestionnaireDTO returnQ = QuestionnaireCreator.createDefaultDTO().build();

            // Mock the "getQuestionnaireByAnswerURL" method of the questionnaire service
            doReturn(returnQ).when(questionnaireService).getQuestionnaireByAnswerURL(ANSWER_URL);

            // Perform GET request to /api/questionnaire/answer/{answerURL}
            mockMvc.perform(get("/api/questionnaire/answer/{answerURL}", ANSWER_URL)
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"title\":\"%s\"".formatted(title))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"description\":\"%s\"".formatted(description))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"isOpen\":%s".formatted(isOpen))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"isPublic\":%s".formatted(isPublic))));
        }

        @Test
        public void closed() throws Exception {
            // Mock the "getQuestionnaireByAnswerURL" method of the questionnaire service
            doThrow(IllegalAccessException.class).when(questionnaireService).getQuestionnaireByAnswerURL(ANSWER_URL);

            // Perform GET request to /api/questionnaire/answer/{answerURL}
            mockMvc.perform(get("/api/questionnaire/answer/{answerURL}", ANSWER_URL)
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        public void notFound() throws Exception {
            // Mock the "getQuestionnaireByAnswerURL" method of the questionnaire service
            doThrow(EntityNotFoundException.class).when(questionnaireService).getQuestionnaireByAnswerURL(ANSWER_URL);

            // Perform GET request to /api/questionnaire/answer/{answerURL}
            mockMvc.perform(get("/api/questionnaire/answer/{answerURL}", ANSWER_URL)
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @NoArgsConstructor
    public class GetQuestionnaireResults {
        final static private String RESULTS_URL = "testResultsURL";

        @Test
        public void success() throws Exception {
            // Init a questionnaire for the "getQuestionnaireByResultsURL" return value
            final QuestionnaireWithResultsDTO returnQ = QuestionnaireCreator.createDefaultDTOWithResults().build();

            // Mock the "getQuestionnaireByResultsURL" method of the questionnaire service
            doReturn(returnQ).when(questionnaireService).getQuestionnaireByResultsURL(testUsername, RESULTS_URL);

            // Perform GET request to /api/questionnaire/results/{resultsURL}
            mockMvc.perform(get("/api/questionnaire/results/{resultsURL}", RESULTS_URL)
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"title\":\"%s\"".formatted(title))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"description\":\"%s\"".formatted(description))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"isOpen\":%s".formatted(isOpen))))
                    .andExpect(MockMvcResultMatchers.content()
                            .string(containsString("\"isPublic\":%s".formatted(isPublic))));
        }

        @Test
        public void notFound() throws Exception {
            // Mock the "getQuestionnaireByResultsURL" method of the questionnaire service
            doThrow(EntityNotFoundException.class)
                    .when(questionnaireService).getQuestionnaireByResultsURL(testUsername, RESULTS_URL);

            // Perform GET request to /api/questionnaire/results/{resultsURL}
            mockMvc.perform(get("/api/questionnaire/results/{resultsURL}", RESULTS_URL)
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(token))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
    }

    @Nested
    @NoArgsConstructor
    public class AnswerQuestionnaire {
        final static private String ANSWER_URL = "testAnswerURL";

        @Test
        public void success() throws Exception {
            // Init test questionnaire response DTO
            final QuestionnaireResponseDTO bodyQ = QuestionnaireResponseDTO.builder().build();

            // Mock the "answerQuestionnaire" method of the questionnaire service
            doNothing().when(questionnaireService)
                    .answerQuestionnaire(eq(ANSWER_URL), any(QuestionnaireResponseDTO.class));

            // Perform POST request to /api/questionnaire/answer/{answerURL}
            mockMvc.perform(post("/api/questionnaire/answer/{answerURL}", ANSWER_URL)
                            .principal(token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bodyQ)))
                    .andExpect(MockMvcResultMatchers.status().isCreated());
        }

        @Test
        public void closed() throws Exception {
            // Init test questionnaire response DTO
            final QuestionnaireResponseDTO bodyQ = QuestionnaireResponseDTO.builder().build();

            // Mock the "answerQuestionnaire" method of the questionnaire service
            doThrow(IllegalAccessException.class).when(questionnaireService)
                    .answerQuestionnaire(eq(ANSWER_URL), any(QuestionnaireResponseDTO.class));

            // Perform POST request to /api/questionnaire/answer/{answerURL}
            mockMvc.perform(post("/api/questionnaire/answer/{answerURL}", ANSWER_URL)
                            .principal(token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bodyQ)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        public void notFound() throws Exception {
            // Init test questionnaire response DTO
            final QuestionnaireResponseDTO bodyQ = QuestionnaireResponseDTO.builder().build();

            // Mock the "answerQuestionnaire" method of the questionnaire service
            doThrow(EntityNotFoundException.class).when(questionnaireService)
                    .answerQuestionnaire(eq(ANSWER_URL), any(QuestionnaireResponseDTO.class));

            // Perform POST request to /api/questionnaire/answer/{answerURL}
            mockMvc.perform(post("/api/questionnaire/answer/{answerURL}", ANSWER_URL)
                            .principal(token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bodyQ)))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        public void invalidResponseType() throws Exception {
            // Init test questionnaire response DTO
            final QuestionnaireResponseDTO bodyQ = QuestionnaireResponseDTO.builder().build();

            // Mock the "answerQuestionnaire" method of the questionnaire service
            doThrow(IllegalArgumentException.class).when(questionnaireService)
                    .answerQuestionnaire(eq(ANSWER_URL), any(QuestionnaireResponseDTO.class));

            // Perform POST request to /api/questionnaire/answer/{answerURL}
            mockMvc.perform(post("/api/questionnaire/answer/{answerURL}", ANSWER_URL)
                            .principal(token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bodyQ)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }
}
