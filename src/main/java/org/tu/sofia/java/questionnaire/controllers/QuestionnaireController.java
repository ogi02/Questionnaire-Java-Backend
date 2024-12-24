package org.tu.sofia.java.questionnaire.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.tu.sofia.java.questionnaire.dto.QuestionnaireCreationDTO;
import org.tu.sofia.java.questionnaire.dto.QuestionnaireDTO;
import org.tu.sofia.java.questionnaire.entities.QuestionnaireEntity;
import org.tu.sofia.java.questionnaire.schemas.DefaultErrorResponseSchema;
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

    private final QuestionnaireService questionnaireService;

    public QuestionnaireController(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
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
                    description = "Bad request.",
                    responseCode = "400",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> createQuestionnaire(Principal principal, @RequestBody QuestionnaireCreationDTO questionnaireCreationDTO) {
        try {
            // Create the questionnaire
            questionnaireService.createQuestionnaire(principal.getName(), questionnaireCreationDTO);

            // Return 201 response
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            // Return 400 response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    "/api/questionnaire"
            ));
        }

    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    @Operation(description = "Delete a questionnaire.")
    @SecurityRequirement(name = "JWTBearerAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Successfully deleted the questionnaire.",
                    responseCode = "204"
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "User is not an owner or administrator of this questionnaire.",
                    responseCode = "403",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Questionnaire not found.",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> deleteQuestionnaire(Principal principal, @PathVariable("id") Long questionnaireId) {
        try {
            // Delete the questionnaire
            questionnaireService.deleteQuestionnaire(principal.getName(), questionnaireId);

            // Return 204 response
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EntityNotFoundException e) {
            // Return 404 response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "/api/questionnaire"
            ));
        } catch (IllegalAccessException e) {
            // Return 403 response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.FORBIDDEN.value(),
                    e.getMessage(),
                    "/api/questionnaire"
            ));
        }

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
    })
    public ResponseEntity<?> getPublicQuestionnaires() {
        // Get public questionnaires from the questionnaire service
        Set<QuestionnaireDTO> publicQuestionnaires = questionnaireService.findPublicQuestionnaires();

        // Check if the set is empty and return 204 response
        if (publicQuestionnaires.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // Return 200 response with the found questionnaires
        return ResponseEntity.status(HttpStatus.OK).body(publicQuestionnaires);
    }

    @GetMapping(value = "/user", produces = "application/json")
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
    })
    public ResponseEntity<?> getUserQuestionnaires(Principal principal) {
        // Get administrated questionnaires by the user
        Set<QuestionnaireDTO> questionnaireDTOSet = questionnaireService.findUserAdministratedQuestionnaires(principal.getName());

        if (questionnaireDTOSet.isEmpty()) {
            // Return 204 status
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // Return 200 status with body
        return ResponseEntity.status(HttpStatus.OK).body(questionnaireDTOSet);
    }

    @PutMapping(value = "/{id}/state/{isOpen}", produces = "application/json")
    @SecurityRequirement(name = "JWTBearerAuth")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Questionnaire state updated.",
                    responseCode = "204"
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "User is not an administrator of this questionnaire.",
                    responseCode = "403",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Questionnaire not found.",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> updateQuestionnaireState(Principal principal, @PathVariable("id") Long questionnaireId, @PathVariable Boolean isOpen) {
        try {
            // Update questionnaire state
            questionnaireService.updateQuestionnaireState(principal.getName(), questionnaireId, isOpen);

            // Return 204 response
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (EntityNotFoundException e) {
            // Return 404 response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "/api/questionnaire/%d/state/%s".formatted(questionnaireId, isOpen.toString())
            ));
        } catch (IllegalAccessException e) {
            // Return 403 response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.FORBIDDEN.value(),
                    e.getMessage(),
                    "/api/questionnaire/%d/state/%s".formatted(questionnaireId, isOpen.toString())
            ));
        } catch (InvalidDataAccessApiUsageException e) {
            // Return 500 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DefaultErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMostSpecificCause().toString(),
                    "/api/questionnaire/%d/state/%s".formatted(questionnaireId, isOpen.toString())
            ));
        }
    }
//
//    @PostMapping("/{id}/admin/{userId}")
//    @SecurityRequirement(name = "JWTBearerAuth")
//    @ApiResponses(value ={
//            @ApiResponse(
//                    description = "Questionnaire admin.",
//                    responseCode = "200"
//            ),
//            @ApiResponse(
//                    description = "Unauthorized.",
//                    responseCode = "401",
//                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
//            ),
//            @ApiResponse(
//                    description = "Questionnaire or user not found.",
//                    responseCode = "404",
//                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
//            )
//    })
//    public ResponseEntity<?> addAdministratorToQuestionnaire(Principal principal, @PathVariable("id") Long id, @PathVariable Long userId) {
//        try {
//            // get user model for current user
//            UserEntity currentUser = authenticationService.loadUserModelByUsername(principal.getName());
//
//            // get questionnaire from db
//            QuestionnaireEntity questionnaire = questionnaireService.findByQuestionnaireIdAndAdministratorId(id, currentUser.getId());
//
//            // verify that candidate admin exists
//            UserEntity candidateAdmin = authenticationService.getById(userId);
//
//            // add admin to questionnaire
//            questionnaireService.addAdministratorToQuestionnaire(questionnaire.getId(), candidateAdmin.getId());
//
//            // save questionnaire
//            questionnaireService.save(questionnaire);
//
//            // return ok response
//            return ResponseEntity.status(HttpStatus.OK).build();
//
//        } catch (EntityNotFoundException e) {
//            // return error response
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultErrorResponseSchema(
//                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
//                    HttpStatus.NOT_FOUND.value(),
//                    e.getMessage(),
//                    "/api/questionnaire/%d/admin/%s".formatted(id, userId)
//            ));
//        }
//    }
//
//    @GetMapping(value = "/vote/{url}")
//    @ApiResponses(value = {
//            @ApiResponse(
//                    description = "Questionnaire found.",
//                    responseCode = "200"
//            ),
//            @ApiResponse(
//                    description = "Questionnaire not found.",
//                    responseCode = "404",
//                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
//            ),
//            @ApiResponse(
//                    description = "JSON encoding error.",
//                    responseCode = "500",
//                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
//            )
//    })
//    public ResponseEntity<?> getQuestionnaire(@PathVariable("url") String votingUrl) {
//        try {
//            // get questionnaire from db
//            QuestionnaireEntity questionnaire = questionnaireService.findByVotingUrl(votingUrl);
//
//            // create filter for the options
//            SimpleFilterProvider filterProvider = new SimpleFilterProvider();
//            filterProvider.addFilter("optionFilter", SimpleBeanPropertyFilter.serializeAllExcept("votes"));
//            filterProvider.addFilter("questionnaireFilter", SimpleBeanPropertyFilter.serializeAllExcept("votingUrl", "resultsUrl", "isPublic", "isOpen"));
//
//            // init object mapper
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
//            objectMapper.setFilterProvider(filterProvider);
//
//            if (questionnaire.getIsOpen()) {
//                // return ok response
//                return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionnaire));
//            } else {
//                // return no content response
//                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//            }
//
//        } catch (EntityNotFoundException e) {
//            System.out.println(e.getMessage());
//            // return error response
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultErrorResponseSchema(
//                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
//                    HttpStatus.NOT_FOUND.value(),
//                    e.getMessage(),
//                    "/api/questionnaire/user"
//            ));
//        } catch (JsonProcessingException e) {
//            // return error response
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DefaultErrorResponseSchema(
//                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
//                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                    e.getMessage(),
//                    "/api/questionnaire/user"
//            ));
//        }
//    }
//
//    @PostMapping(value = "/vote/{url}")
//    @ApiResponses(value = {
//            @ApiResponse(
//                    description = "Voted.",
//                    responseCode = "201"
//            ),
//            @ApiResponse(
//                    description = "Questionnaire is closed.",
//                    responseCode = "403",
//                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
//            ),
//            @ApiResponse(
//                    description = "Questionnaire, question or option not found.",
//                    responseCode = "404",
//                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
//            )
//    })
//    public ResponseEntity<?> vote(@PathVariable("url") String votingUrl, @RequestBody VoteSchema request) {
//        try {
//            // get questionnaire from db
//            QuestionnaireEntity questionnaire = questionnaireService.findByVotingUrl(votingUrl);
//
//            // check if questionnaire is closed
//            if (!questionnaire.getIsOpen()) {
//                // return no content response
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DefaultErrorResponseSchema(
//                        DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
//                        HttpStatus.FORBIDDEN.value(),
//                        "Questionnaire is closed!",
//                        "/api/questionnaire/vote/%s".formatted(votingUrl)
//                ));
//            }
//
//            // iterate through votes and add vote to every question
//            for (VoteSchema.Vote vote : request.getVotes()) {
//                questionnaire
//                        .findQuestionById(vote.getQuestionId())
//                        .findOptionById(vote.getOptionId())
//                        .addVote();
//            }
//
//            // save questionnaire
//            questionnaireService.save(questionnaire);
//
//            // return created response
//            return ResponseEntity.status(HttpStatus.CREATED).build();
//
//        } catch (EntityNotFoundException e) {
//            // return error response
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultErrorResponseSchema(
//                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
//                    HttpStatus.NOT_FOUND.value(),
//                    e.getMessage(),
//                    "/api/questionnaire/vote/%s".formatted(votingUrl)
//            ));
//        }
//    }
//
//    @GetMapping(value = "/results/{url}")
//    @SecurityRequirement(name = "JWTBearerAuth")
//    @ApiResponses(value = {
//            @ApiResponse(
//                    description = "Results for this questionnaire found.",
//                    responseCode = "200",
//                    content = @Content(schema = @Schema(implementation = QuestionnaireEntity.class))
//            ),
//            @ApiResponse(
//                    description = "Unauthorized.",
//                    responseCode = "401",
//                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
//            ),
//            @ApiResponse(
//                    description = "No access to this questionnaire.",
//                    responseCode = "403",
//                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
//            ),
//            @ApiResponse(
//                    description = "Questionnaire not found.",
//                    responseCode = "404",
//                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
//            ),
//            @ApiResponse(
//                    description = "JSON encoding error.",
//                    responseCode = "500",
//                    content = @Content(schema = @Schema(implementation = DefaultErrorResponseSchema.class))
//            )
//    })
//    public ResponseEntity<?> getQuestionnaireResults(Principal principal, @PathVariable("url") String resultsUrl) {
//        try {
//            // get user model for current user
//            UserEntity currentUser = authenticationService.loadUserModelByUsername(principal.getName());
//
//            // get questionnaire from db
//            QuestionnaireEntity questionnaire = questionnaireService.findByResultsUrlAndAdministratorId(resultsUrl, currentUser.getId());
//
//            // create filter for the options
//            SimpleFilterProvider filterProvider = new SimpleFilterProvider();
//            filterProvider.addFilter("optionFilter", SimpleBeanPropertyFilter.serializeAll());
//            filterProvider.addFilter("questionnaireFilter", SimpleBeanPropertyFilter.serializeAllExcept("votingUrl", "resultsUrl"));
//
//            // init object mapper
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
//            objectMapper.setFilterProvider(filterProvider);
//
//            // return ok response
//            return ResponseEntity.status(HttpStatus.OK).body(objectMapper.writeValueAsString(questionnaire));
//
//        } catch (EntityNotFoundException e) {
//            // return error response
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefaultErrorResponseSchema(
//                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
//                    HttpStatus.FORBIDDEN.value(),
//                    e.getMessage(),
//                    "/api/questionnaire/results/%s".formatted(resultsUrl)
//            ));
//        } catch (JsonProcessingException e) {
//            // return error response
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DefaultErrorResponseSchema(
//                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
//                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                    e.getMessage(),
//                    "/api/questionnaire/results/%s".formatted(resultsUrl)
//            ));
//        }
//    }
}
