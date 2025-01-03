package org.tu.sofia.java.questionnaire.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*; // NOPMD
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireDTO;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireResponseDTO;
import org.tu.sofia.java.questionnaire.dtos.QuestionnaireWithResultsDTO;
import org.tu.sofia.java.questionnaire.schemas.ErrorResponseSchema;
import org.tu.sofia.java.questionnaire.services.QuestionnaireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.persistence.EntityNotFoundException;

import java.security.Principal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@RestController
@RequestMapping("/api/questionnaire")
@Tag(name = "Questionnaire")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    public QuestionnaireController(final QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
    @Operation(description = "Create a questionnaire with questions and options.")
    @SecurityRequirement(name = "JWTBearerAuth")
    @ApiResponses({
            @ApiResponse(
                    description = "Successfully created a new questionnaire.",
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = QuestionnaireWithResultsDTO.class))
            ),
            @ApiResponse(
                    description = "Bad request.",
                    responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> createQuestionnaire(
            final Principal principal, final @RequestBody QuestionnaireDTO questionnaireDTO
    ) {
        try {
            // Create the questionnaire
            final QuestionnaireWithResultsDTO questionnaire =
                    questionnaireService.createQuestionnaire(principal.getName(), questionnaireDTO);

            // Return 201 response
            return ResponseEntity.status(HttpStatus.CREATED).body(questionnaire);
        } catch (Exception e) {
            // Return 400 response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    "/api/questionnaire",
                    HttpMethod.POST.name()
            ));
        }

    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    @Operation(description = "Delete a questionnaire.")
    @SecurityRequirement(name = "JWTBearerAuth")
    @ApiResponses({
            @ApiResponse(
                    description = "Successfully deleted the questionnaire.",
                    responseCode = "204"
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "User is not an owner or administrator of this questionnaire.",
                    responseCode = "403",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Questionnaire not found.",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> deleteQuestionnaire(
            final Principal principal, @PathVariable("id") final Long questionnaireId
    ) {
        try {
            // Delete the questionnaire
            questionnaireService.deleteQuestionnaire(principal.getName(), questionnaireId);

            // Return 204 response
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EntityNotFoundException e) {
            // Return 404 response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "/api/questionnaire",
                    HttpMethod.DELETE.name()
            ));
        } catch (IllegalAccessException e) {
            // Return 403 response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.FORBIDDEN.value(),
                    e.getMessage(),
                    "/api/questionnaire",
                    HttpMethod.DELETE.name()
            ));
        }

    }

    @GetMapping(value = "/public", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    description = "Questionnaires found.",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = QuestionnaireDTO.class))
            ),
            @ApiResponse(
                    description = "No Questionnaires found.",
                    responseCode = "204"
            ),
    })
    public ResponseEntity<?> getPublicQuestionnaires() {
        // Get public questionnaires from the questionnaire service
        final Set<QuestionnaireDTO> publicQuestionnaires = questionnaireService.getPublicQuestionnaires();

        // Check if the set is empty and return 204 response
        if (publicQuestionnaires.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // Return 200 response with the found questionnaires
        return ResponseEntity.status(HttpStatus.OK).body(publicQuestionnaires);
    }

    @GetMapping(value = "/user", produces = "application/json")
    @SecurityRequirement(name = "JWTBearerAuth")
    @ApiResponses({
            @ApiResponse(
                    description = "Questionnaires found.",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = QuestionnaireDTO.class))
            ),
            @ApiResponse(
                    description = "No Questionnaires found.",
                    responseCode = "204"
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> getUserQuestionnaires(final Principal principal) {
        // Get administrated questionnaires by the user
        final Set<QuestionnaireWithResultsDTO> questionnairesWithResults =
                questionnaireService.getUserAdministratedQuestionnaires(principal.getName());

        if (questionnairesWithResults.isEmpty()) {
            // Return 204 status
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        // Return 200 status with body
        return ResponseEntity.status(HttpStatus.OK).body(questionnairesWithResults);
    }

    @PutMapping(value = "/{questionnaireId}/state/{isOpen}", produces = "application/json")
    @SecurityRequirement(name = "JWTBearerAuth")
    @ApiResponses({
            @ApiResponse(
                    description = "Questionnaire state updated.",
                    responseCode = "204"
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "User is not an administrator of this questionnaire.",
                    responseCode = "403",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Questionnaire not found.",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> updateQuestionnaireState(
            final Principal principal, @PathVariable final Long questionnaireId, @PathVariable final Boolean isOpen
    ) {
        try {
            // Update questionnaire state
            questionnaireService.updateQuestionnaireState(principal.getName(), questionnaireId, isOpen);

            // Return 204 response
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (EntityNotFoundException e) {
            // Return 404 response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "/api/questionnaire/%d/state/%s".formatted(questionnaireId, isOpen.toString()),
                    HttpMethod.PUT.name()
            ));
        } catch (IllegalAccessException e) {
            // Return 403 response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.FORBIDDEN.value(),
                    e.getMessage(),
                    "/api/questionnaire/%d/state/%s".formatted(questionnaireId, isOpen.toString()),
                    HttpMethod.PUT.name()
            ));
        }
    }

    @PostMapping(value = "/{questionnaireId}/admin/{userId}", produces = "application/json")
    @SecurityRequirement(name = "JWTBearerAuth")
    @ApiResponses({
            @ApiResponse(
                    description = "Questionnaire admin added successfully.",
                    responseCode = "204"
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Current user is not an administrator of the questionnaire.",
                    responseCode = "403",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Questionnaire ID or user ID not found.",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            )
    })
    public ResponseEntity<?> addAdministratorToQuestionnaire(
            final Principal principal, @PathVariable final Long questionnaireId, @PathVariable final Long userId
    ) {
        try {
            // Add administrator to questionnaire
            questionnaireService.addAdministratorToQuestionnaire(principal.getName(), questionnaireId, userId);

            // Return 204 response
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (EntityNotFoundException e) {
            // Return 404 response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "/api/questionnaire/%d/admin/%s".formatted(questionnaireId, userId),
                    HttpMethod.POST.name()
            ));
        } catch (IllegalAccessException e) {
            // Return 403 response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.FORBIDDEN.value(),
                    e.getMessage(),
                    "/api/questionnaire/%d/admin/%s".formatted(questionnaireId, userId),
                    HttpMethod.POST.name()
            ));
        }
    }

    @GetMapping(value = "/answer/{answerURL}", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    description = "Questionnaire found.",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = QuestionnaireDTO.class))
            ),
            @ApiResponse(
                    description = "Questionnaire is closed.",
                    responseCode = "403",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Questionnaire not found.",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> getQuestionnaireByAnswerURL(@PathVariable final String answerURL) {
        try {
            // Get the questionnaire from the DB
            final QuestionnaireDTO questionnaire = questionnaireService.getQuestionnaireByAnswerURL(answerURL);

            // Return 200 response
            return ResponseEntity.status(HttpStatus.OK).body(questionnaire);

        } catch (EntityNotFoundException e) {
            // Return 404 response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "/api/questionnaire/answer/%s".formatted(answerURL),
                    HttpMethod.GET.name()
            ));
        } catch (IllegalAccessException e) {
            // Return 403 response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.FORBIDDEN.value(),
                    e.getMessage(),
                    "/api/questionnaire/answer/%s".formatted(answerURL),
                    HttpMethod.GET.name()
            ));
        }
    }

    @GetMapping(value = "/results/{resultsURL}", produces = "application/json")
    @SecurityRequirement(name = "JWTBearerAuth")
    @ApiResponses({
            @ApiResponse(
                    description = "Results for this questionnaire found.",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = QuestionnaireWithResultsDTO.class))
            ),
            @ApiResponse(
                    description = "Unauthorized.",
                    responseCode = "401",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Questionnaire results not found or user has no access to them.",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
    })
    public ResponseEntity<?> getQuestionnaireResults(final Principal principal, @PathVariable final String resultsURL) {
        try {
            // Get the questionnaire results from DB
            final QuestionnaireWithResultsDTO questionnaireWithResults =
                    questionnaireService.getQuestionnaireByResultsURL(principal.getName(), resultsURL);

            // Return 200 response
            return ResponseEntity.status(HttpStatus.OK).body(questionnaireWithResults);

        } catch (EntityNotFoundException e) {
            // Return 404 response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "/api/questionnaire/results/%s".formatted(resultsURL),
                    HttpMethod.GET.name()
            ));
        }
    }

    @PostMapping(value = "/answer/{answerURL}", consumes = "application/json", produces = "application/json")
    @ApiResponses({
            @ApiResponse(
                    description = "Successfully answered.",
                    responseCode = "201"
            ),
            @ApiResponse(
                    description = "Unexpected response type for any of the questions.",
                    responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Questionnaire is closed.",
                    responseCode = "403",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            ),
            @ApiResponse(
                    description = "Questionnaire ID, question ID or option ID was not found",
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponseSchema.class))
            )
    })
    public ResponseEntity<?> answerQuestionnaire(
            @PathVariable final String answerURL, @RequestBody final QuestionnaireResponseDTO questionnaireResponseDTO
    ) {
        try {
            // Save a response on the questionnaire
            questionnaireService.answerQuestionnaire(answerURL, questionnaireResponseDTO);

            // Return 201 response
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (EntityNotFoundException e) {
            // Return 404 response
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "/api/questionnaire/answer/%s".formatted(answerURL),
                    HttpMethod.POST.name()
            ));
        } catch (IllegalAccessException e) {
            // Return 403 response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.FORBIDDEN.value(),
                    e.getMessage(),
                    "/api/questionnaire/answer/%s".formatted(answerURL),
                    HttpMethod.POST.name()
            ));
        } catch (IllegalArgumentException e) {
            // Return 400 response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseSchema(
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    "/api/questionnaire/answer/%s".formatted(answerURL),
                    HttpMethod.POST.name()
            ));
        }
    }
}
