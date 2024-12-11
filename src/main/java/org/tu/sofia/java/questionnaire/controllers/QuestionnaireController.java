package org.tu.sofia.java.questionnaire.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class QuestionnaireController {

    private final AuthenticationService authenticationService;
    private final QuestionnaireService questionnaireService;

    public QuestionnaireController(AuthenticationService authenticationService, QuestionnaireService questionnaireService) {
        this.authenticationService = authenticationService;
        this.questionnaireService = questionnaireService;
    }

    @GetMapping(value = "/public", produces = "application/json")
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

        // save questionnaire
        questionnaireService.save(questionnaire);

        // return created response
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(value = "/{id}/state/{isOpen}", produces = "application/json")
    public ResponseEntity<?> changeQuestionnaireState(Principal principal, @PathVariable("id") Long id, @PathVariable Boolean isOpen) {
        try {
            // get user model for current user
            UserEntity currentUser = authenticationService.loadUserModelByUsername(principal.getName());

            // get questionnaire from db
            QuestionnaireEntity questionnaire = questionnaireService.findByQuestionnaireIdAndOwnerId(id, currentUser.getId());

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

    @GetMapping(value = "/vote/{url}")
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
    public ResponseEntity<?> getQuestionnaireResults(Principal principal, @PathVariable("url") String resultsUrl) {
        try {
            // get user model for current user
            UserEntity currentUser = authenticationService.loadUserModelByUsername(principal.getName());

            // get questionnaire from db
            QuestionnaireEntity questionnaire = questionnaireService.findByResultsUrlAndOwnerId(resultsUrl, currentUser.getId());

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
