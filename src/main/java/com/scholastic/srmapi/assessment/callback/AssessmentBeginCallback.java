package com.scholastic.srmapi.assessment.callback;

import com.scholastic.aa.api.IAssessment;
import com.scholastic.srmapi.model.AssessmentTestDetails;
import com.scholastic.srmapi.model.UserSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class AssessmentBeginCallback {

    private UserSession session;

    /**
     * Starts the assessment for the user. Currently returns a dummy assessment which doesn't save to the DB.
     * @param assessment Assessment to begin.
     */
    public void beginAssessment(IAssessment assessment) {
        log.warn("Beginning assessment callback for assessment, to create a new one. This is where the table insertion happens.");
        session.setAssessmentTestDetails(buildDummyAssessment(assessment.getId()));
    }

    private AssessmentTestDetails buildDummyAssessment(Long id) {
        var assessment = AssessmentBuilderHelper.buildFullAssessmentDetails();
        assessment.setId(id);
        return assessment;
    }

}
