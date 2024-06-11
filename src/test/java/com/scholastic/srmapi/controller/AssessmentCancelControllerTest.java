package com.scholastic.srmapi.controller;

import com.scholastic.srmapi.service.support.AssessmentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AssessmentCancelControllerTest {
    @Mock
    private AssessmentServiceImpl assessmentServiceImpl;

    @InjectMocks
    private AssessmentCancelController assessmentCancelController;

    @Test
    @DisplayName("Cancel outdated assessments")
    void cancelOutdatedSRMAssessments() {
        var response = assessmentCancelController.cancelOutdatedSRMAssessments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OK", response.getBody());
        verify(assessmentServiceImpl, times(1)).cancelAssessments();
    }
}
