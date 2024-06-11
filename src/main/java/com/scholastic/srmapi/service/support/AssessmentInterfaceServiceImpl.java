package com.scholastic.srmapi.service.support;

import com.scholastic.aa.AssessmentAlgorithm;
import com.scholastic.aa.api.IAssessmentAlgorithm;
import com.scholastic.aa.api.enums.Grade;
import com.scholastic.srmapi.assessment.callback.LitproUSAssessmentCallback;
import com.scholastic.srmapi.model.UserSession;
import com.scholastic.srmapi.service.AssessmentInterfaceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.time.Instant;
import java.util.Date;

@Service
@RequestScope
@AllArgsConstructor
public class AssessmentInterfaceServiceImpl implements AssessmentInterfaceService {

    private LitproUSAssessmentCallback litproUSAssessmentCallback;

    private UserSession userSession;

    @Override
    public IAssessmentAlgorithm getIAssessmentAlgorithm() {
        IAssessmentAlgorithm assessment = new AssessmentAlgorithm();
        assessment.setCallback(litproUSAssessmentCallback);
        return assessment;
    }

    @Override
    public void prepareAndStartAssessment() {
        var algo = getIAssessmentAlgorithm();

        // Originates in the BAT admin table, defaulting for now.
        algo.setGrade(Grade.GRADE_1);

        // Returned from BAT call on organization table, defaulting for now.
        algo.setSchoolYearStart(Date.from(Instant.now()));

        // Returned from BAT call on assessment
        algo.setMeasure(userSession.getLexileScore());
        algo.setTeacherJudgement(userSession.getTeacherJudgement());

        // LIT-5044 - change behavior to always give practice
        algo.setPractice(true);
        algo.setPracticeEnabled(true);

        algo.start();
    }
}
