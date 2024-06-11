package com.scholastic.srmapi.service.support;

import com.scholastic.srmapi.config.BaWebClientConfig;
import com.scholastic.srmapi.exception.AssessmentException;
import com.scholastic.srmapi.response.Assessment;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest(classes = { BatchAssignmentServiceImpl.class, BaWebClientConfig.class})
class BatchAssignmentServiceImplTest {

    private static BatchAssignmentServiceImpl assignmentServiceImpl;


    public static MockWebServer mockWebServer;


    @Autowired
    public BaWebClientConfig config;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8091);
    }

    @BeforeEach
    void setup() {
        assignmentServiceImpl = new BatchAssignmentServiceImpl(config.baWebClient());
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getStudentAssessment() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("{\"srmStatus\":{\"id\":2,\"studentId\":700,\"timeSpent\":5}}");
        mockWebServer.enqueue(mockResponse);

        var result = assignmentServiceImpl.getStudentAssessment(7L);
        Assessment assessment = result.block();

        assertNotNull(assessment);
        assertNotNull(assessment.getSrmStatus());
        assertEquals(2, assessment.getSrmStatus().getId());
        assertEquals(700, assessment.getSrmStatus().getStudentId());
        assertEquals(5, assessment.getSrmStatus().getTimeSpent());
    }

    @Test
    void getStudentAssessment_exception() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(404)
                .setBody("No assessment found");
        mockWebServer.enqueue(mockResponse);

        var result = assignmentServiceImpl.getStudentAssessment(7L);

        AssessmentException ae = assertThrows(AssessmentException.class,
                result::block,
                "Wrong assessment type");

        assertEquals(HttpStatus.NOT_FOUND, ae.getStatus());
        assertEquals("Error retrieving assessment", ae.getExceptionMessage());
    }

    @Test
    void getStudentAssessment_validateBATRequestDetailsAreCorrect() throws InterruptedException {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("{\"srmStatus\":{\"id\":2,\"studentId\":700,\"timeSpent\":5}}");
        mockWebServer.enqueue(mockResponse);

        assignmentServiceImpl.getStudentAssessment(7L).block();

        var originalRequest = mockWebServer.takeRequest();

        assertNotNull(originalRequest.getHeaders().get("authorization"));
        assertEquals("Bearer " + config.getKongToken(), originalRequest.getHeaders().get("authorization"));
        assertNotNull(originalRequest.getRequestUrl());
        assertEquals("/api/v1/assessments/srm/7", originalRequest.getPath());
    }

    @Test
    void cancelOutdatedAssessments_error() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(404);
        mockWebServer.enqueue(mockResponse);

        Set<Long> assessmentTestIds = new HashSet<>();
        assessmentTestIds.add(1l);

        var result = assignmentServiceImpl.cancelOutdatedAssessments(assessmentTestIds);

        AssessmentException ae = assertThrows(AssessmentException.class,
                result::block,
                "Error from batch assignment");

        assertEquals(HttpStatus.NOT_FOUND, ae.getStatus());
        assertEquals("Error canceling assessment", ae.getExceptionMessage());
    }

    @Test
    void cancelOutdatedAssessments_noBody() {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200);
        mockWebServer.enqueue(mockResponse);

        Set<Long> assessmentTestIds = new HashSet<>();
        assessmentTestIds.add(1l);

        assertNull(assignmentServiceImpl.cancelOutdatedAssessments(assessmentTestIds).block());
    }
}
