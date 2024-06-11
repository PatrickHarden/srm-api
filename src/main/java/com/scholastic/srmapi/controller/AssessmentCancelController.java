package com.scholastic.srmapi.controller;

import com.scholastic.srmapi.service.AssessmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assessments")
@AllArgsConstructor
public class AssessmentCancelController {

    private AssessmentService assessmentService;

    @PutMapping(value = "/cancelOutdatedSRMAssessments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> cancelOutdatedSRMAssessments() {

        assessmentService.cancelAssessments();

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
