package com.scholastic.srmapi.service.support;

import com.scholastic.srmapi.exception.AssessmentException;
import com.scholastic.srmapi.response.Assessment;
import com.scholastic.srmapi.service.BatchAssignmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@AllArgsConstructor
public class BatchAssignmentServiceImpl implements BatchAssignmentService {

    private final WebClient baWebClient;


    @Override
    public Mono<Assessment> getStudentAssessment(Long studentId) {
            return baWebClient
                    .get()
                    .uri( "/api/v1/assessments/srm/" + studentId)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError,
                            clientResponse -> Mono.error(new AssessmentException(clientResponse.statusCode(), "Error retrieving assessment"))
                    ).bodyToMono(Assessment.class);
    }

    @Override
    public Mono<Void> cancelOutdatedAssessments(Set<Long> assessmentTestIds) {
        return baWebClient
                .put()
                .uri("/api/v1/assessments/cancelAssessments")
                .bodyValue(assessmentTestIds)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> Mono.error(new AssessmentException(clientResponse.statusCode(), "Error canceling assessment")))
                .bodyToMono(Void.class);
    }

}
