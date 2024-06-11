package com.scholastic.srmapi.controller;

import com.scholastic.srmapi.config.CurrentUser;
import com.scholastic.srmapi.model.AssessmentTestDetails;
import com.scholastic.srmapi.model.User;
import com.scholastic.srmapi.model.UserSession;
import com.scholastic.srmapi.response.Assessment;
import com.scholastic.srmapi.response.AssessmentTestResponse;
import com.scholastic.srmapi.service.AssessmentInterfaceService;
import com.scholastic.srmapi.service.AssessmentService;
import com.scholastic.srmapi.service.BatchAssignmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/assessment")
@AllArgsConstructor
public class AssessmentController {

    private BatchAssignmentService batchAssignmentService;

    private AssessmentService assessmentService;

    private AssessmentInterfaceService assessmentInterfaceService;

    private UserSession session;


    /**
     *
     * @param studentId Long representation of a student's ID
     * @return Assessment details about assigned assessments
     */
    @GetMapping( "/{studentId}")
    public Mono<Assessment> getStudentAssessment(@PathVariable("studentId") Long studentId) {
        return batchAssignmentService.getStudentAssessment(studentId);
    }

    /**
     * Gets the current assessment for a student, referencing the source ID from the JWT.
     * @param studentId Long representation of a student's ID
     * @param user The {@code CurrentUser} of the incoming request.
     * @return The {@code ResponseEntity} containing the requested {@code AssessmentTestResponse}
     */
    @GetMapping(value = "/{studentId}/current")
    public ResponseEntity<AssessmentTestResponse> getCurrentAssessment(
            @PathVariable("studentId") Long studentId,
            @CurrentUser User user) {
        // Retrieve current assessment
        AssessmentTestDetails assessment = assessmentService.getCurrentAssessment(studentId);

        // Associate current user session to newest question.
        assessmentService.updateAssessmentSession(user.getSessionId(), assessment);

        // Return assessment response
        return ResponseEntity.ok(AssessmentTestResponse.buildAssessmentResponse(assessment));
    }

    /**
     * Begins the assessment assigned to a user, or returns the next question of the assessment.
     * @param studentId Long representation of a student's ID
     * @param user The {@code CurrentUser} of the incoming request.
     * @return The {@code ResponseEntity} containing the requested {@code AssessmentTestResponse}
     */
    @PostMapping(value = "/{studentId}")
    public ResponseEntity<AssessmentTestResponse> beginAssessment(
            @PathVariable("studentId") Long studentId,
            @CurrentUser User user) {

        // Populate assessment/user details in session
        assessmentService.populateSessionAssessment(user);

        // Begin the test
        assessmentInterfaceService.prepareAndStartAssessment();

        // Associate current user session to the newest question once we save on beginning the test.
        // (to be done once assessment is being saved in callback).
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AssessmentTestResponse.buildAssessmentResponse(session.getAssessmentTestDetails()));
    }

}
