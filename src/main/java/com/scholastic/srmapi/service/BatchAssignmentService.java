package com.scholastic.srmapi.service;

import com.scholastic.srmapi.response.Assessment;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface BatchAssignmentService {

    Mono<Assessment> getStudentAssessment(Long studentId);

    Mono<Void> cancelOutdatedAssessments(Set<Long> assessmentTestIds);
}
