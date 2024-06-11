package com.scholastic.srmapi.assessment.callback;

import com.scholastic.aa.api.IAssessment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class AssessmentResumeCallback {

    /**
     * Resume the given assessment from where it was suspended.
     * @param assessment assessment to resume.
     */
    public void resumeAssessment(IAssessment assessment) {
        log.warn("Resuming assessment with ID of {}", assessment.getId());
        // To be filled
    }

}
