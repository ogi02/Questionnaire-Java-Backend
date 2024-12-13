package org.tu.sofia.java.questionnaire.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.entities.UserEntity;
import org.tu.sofia.java.questionnaire.schemas.DefaultErrorResponseSchema;
import org.tu.sofia.java.questionnaire.schemas.VoteSchema;
import org.tu.sofia.java.questionnaire.services.AuthenticationService;
import org.tu.sofia.java.questionnaire.services.QuestionnaireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

import java.security.Principal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/questionnaire")
@Tag(name = "Questionnaire")
public class QuestionnaireController {

    private final AuthenticationService authenticationService;
    private final QuestionnaireService questionnaireService;

    public QuestionnaireController(AuthenticationService authenticationService, QuestionnaireService questionnaireService) {
        this.authenticationService = authenticationService;
        this.questionnaireService = questionnaireService;
    }

    @GetMapping(value = "/public", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Questionnaires found.",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = QuestionnaireEntity.class))
            ),
            @ApiResponse(
                    description = "No Questionnaires found.",
                    responseCode = "204"
            ),
            @ApiResponse(
                    description = "JSON encoding error.",
                    responseCode = "500",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> getPublicQuestionnaires() {
        try {
            // get public questionnaires from db
            Set<QuestionnaireEntity> publicQuestionnaires = questionnaireService.findPublic();

            // check for empty set
            if (publicQuestionnaires.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            // create filter for the options
            SimpleFilterProvider filterProvider = new SimpleFilterProvider();
            filterProvider.addFilter("questionnaireFilter", SimpleBeanPropertyFilter.serializeAllExcept("questions", "resultsUrl", "isOpen", "isPublic"));

            // init object mapper
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setFilterProvider(filterProvider);

            // return ok response
            return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(publicQuestionnaires));

        } catch (JsonProcessingException e) {
            // return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "/api/questionnaire/public"
            ));
        }
    }

    @GetMapping(value = "/user")
    @SecurityRequirement(name = "JWTBearerAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Questionnaires found.",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = QuestionnaireEntity.class))
            ),
            @ApiResponse(
                    description = "No Questionnaires found.",
                    responseCode = "204"
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "JSON encoding error.",
                    responseCode = "500",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> getUserQuestionnaires(Principal principal) {
        try {
            // get user model for current user
            UserEntity currentUser = authenticationService.loadUserModelByUsername(principal.getName());

            // get user questionnaires from db
            Set<QuestionnaireEntity> userQuestionnaires = questionnaireService.findByOwner(currentUser);

            // check for empty set
            if (userQuestionnaires.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            // create filter for the options
            SimpleFilterProvider filterProvider = new SimpleFilterProvider();
            filterProvider.addFilter("questionnaireFilter", SimpleBeanPropertyFilter.serializeAllExcept("questions"));

            // init object mapper
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setFilterProvider(filterProvider);

            // return ok response
            return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(userQuestionnaires));

        } catch (JsonProcessingException e) {
            // return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "/api/questionnaire/user"
            ));
        }
    }

    @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
    @Operation(description = "Create a questionnaire with questions and options.")
    @SecurityRequirement(name = "JWTBearerAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Successfully created a new questionnaire.",
                    responseCode = "201"
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> createQuestionnaire(Principal principal, @Valid @RequestBody QuestionnaireEntity questionnaire) {
        // get user model for current user
        UserEntity currentUser = authenticationService.loadUserModelByUsername(principal.getName());

        questionnaire.getQuestions().forEach(question -> {
            // link questionnaire to question
            question.setQuestionnaire(questionnaire);

            // link question to options
            question.getOptions().forEach(option -> option.setQuestion(question));
        });

        // set questionnaire's owner
        questionnaire.setOwner(currentUser);
        questionnaire.getAdministrators().add(currentUser);

        // save questionnaire
        questionnaireService.save(questionnaire);

        // return created response
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "/{id}/state/{isOpen}", produces = "application/json")
    @SecurityRequirement(name = "JWTBearerAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Questionnaire state updated.",
                    responseCode = "200"
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Questionnaire not found.",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            )
    })
    public ResponseEntity<?> changeQuestionnaireState(Principal principal, @PathVariable("id") Long id, @PathVariable Boolean isOpen) {
        try {
            // get user model for current user
            UserEntity currentUser = authenticationService.loadUserModelByUsername(principal.getName());

            // get questionnaire from db
            QuestionnaireEntity questionnaire = questionnaireService.findByQuestionnaireIdAndAdministratorId(id, currentUser.getId());

            // change questionnaire state
            questionnaire.setIsOpen(isOpen);

            // save questionnaire
            questionnaireService.save(questionnaire);

            // return ok response
            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (EntityNotFoundException e) {
            // return error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "/api/questionnaire/%d/state/%s".formatted(id, isOpen.toString())
            ));
        }
    }

    @PostMapping("/{id}/admin/{userId}")
    @SecurityRequirement(name = "JWTBearerAuth")
    @ApiResponses(value ={
            @ApiResponse(
                    description = "Questionnaire admin.",
                    responseCode = "200"
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Questionnaire or user not found.",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            )
    })
    public ResponseEntity<?> addAdministratorToQuestionnaire(Principal principal, @PathVariable("id") Long id, @PathVariable Long userId) {
        try {
            // get user model for current user
            UserEntity currentUser = authenticationService.loadUserModelByUsername(principal.getName());

            // get questionnaire from db
            QuestionnaireEntity questionnaire = questionnaireService.findByQuestionnaireIdAndAdministratorId(id, currentUser.getId());

            // verify that candidate admin exists
            UserEntity candidateAdmin = authenticationService.getById(userId);

            // add admin to questionnaire
            questionnaireService.addAdministratorToQuestionnaire(questionnaire.getId(), candidateAdmin.getId());

            // save questionnaire
            questionnaireService.save(questionnaire);

            // return ok response
            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (EntityNotFoundException e) {
            // return error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "/api/questionnaire/%d/admin/%s".formatted(id, userId)
            ));
        }
    }

    @GetMapping(value = "/vote/{url}")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Questionnaire found.",
                    responseCode = "200"
            ),
            @ApiResponse(
                    description = "Questionnaire not found.",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "JSON encoding error.",
                    responseCode = "500",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            )
    })
    public ResponseEntity<?> getQuestionnaire(@PathVariable("url") String votingUrl) {
        try {
            // get questionnaire from db
            QuestionnaireEntity questionnaire = questionnaireService.findByVotingUrl(votingUrl);

            // create filter for the options
            SimpleFilterProvider filterProvider = new SimpleFilterProvider();
            filterProvider.addFilter("optionFilter", SimpleBeanPropertyFilter.serializeAllExcept("votes"));
            filterProvider.addFilter("questionnaireFilter", SimpleBeanPropertyFilter.serializeAllExcept("votingUrl", "resultsUrl", "isPublic", "isOpen"));

            // init object mapper
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setFilterProvider(filterProvider);

            if (questionnaire.getIsOpen()) {
                // return ok response
                return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionnaire));
            } else {
                // return no content response
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
            // return error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "/api/questionnaire/user"
            ));
        } catch (JsonProcessingException e) {
            // return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "/api/questionnaire/user"
            ));
        }
    }

    @PostMapping(value = "/vote/{url}")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Voted.",
                    responseCode = "201"
            ),
            @ApiResponse(
                    description = "Questionnaire is closed.",
                    responseCode = "403",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Questionnaire, question or option not found.",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            )
    })
    public ResponseEntity<?> vote(@PathVariable("url") String votingUrl, @RequestBody VoteSchema request) {
        try {
            // get questionnaire from db
            QuestionnaireEntity questionnaire = questionnaireService.findByVotingUrl(votingUrl);

            // check if questionnaire is closed
            if (!questionnaire.getIsOpen()) {
                // return no content response
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DefaultErrorResponseSchema(
                        DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                        HttpStatus.FORBIDDEN.value(),
                        "Questionnaire is closed!",
                        "/api/questionnaire/vote/%s".formatted(votingUrl)
                ));
            }

            // iterate through votes and add vote to every question
            for (VoteSchema.Vote vote : request.getVotes()) {
                questionnaire
                        .findQuestionById(vote.getQuestionId())
                        .findOptionById(vote.getOptionId())
                        .addVote();
            }

            // save questionnaire
            questionnaireService.save(questionnaire);

            // return created response
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (EntityNotFoundException e) {
            // return error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "/api/questionnaire/vote/%s".formatted(votingUrl)
            ));
        }
    }

    @GetMapping(value = "/results/{url}")
    @SecurityRequirement(name = "JWTBearerAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Results for this questionnaire found.",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = QuestionnaireEntity.class))
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "No access to this questionnaire.",
                    responseCode = "403",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Questionnaire not found.",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "JSON encoding error.",
                    responseCode = "500",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            )
    })
    public ResponseEntity<?> getQuestionnaireResults(Principal principal, @PathVariable("url") String resultsUrl) {
        try {
            // get user model for current user
            UserEntity currentUser = authenticationService.loadUserModelByUsername(principal.getName());

            // get questionnaire from db
            QuestionnaireEntity questionnaire = questionnaireService.findByResultsUrlAndAdministratorId(resultsUrl, currentUser.getId());

            // create filter for the options
            SimpleFilterProvider filterProvider = new SimpleFilterProvider();
            filterProvider.addFilter("optionFilter", SimpleBeanPropertyFilter.serializeAll());
            filterProvider.addFilter("questionnaireFilter", SimpleBeanPropertyFilter.serializeAllExcept("votingUrl", "resultsUrl"));

            // init object mapper
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setFilterProvider(filterProvider);

            // return ok response
            return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionnaire));

        } catch (EntityNotFoundException e) {
            // return error response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.FORBIDDEN.value(),
                    e.getMessage(),
                    "/api/questionnaire/results/%s".formatted(resultsUrl)
            ));
        } catch (JsonProcessingException e) {
            // return error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "/api/questionnaire/results/%s".formatted(resultsUrl)
            ));
        }
    }
}
