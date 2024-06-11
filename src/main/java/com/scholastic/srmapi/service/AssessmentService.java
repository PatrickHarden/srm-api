package com.scholastic.srmapi.service;

import com.scholastic.aa.api.enums.TestState;
import com.scholastic.srmapi.model.AssessmentTestDetails;
import com.scholastic.srmapi.model.User;

import java.util.List;

/**
 * Service class responsible for administering queries and mutators for a student's SRM assessment
 */
public interface AssessmentService {

    /**
     * Retrieves the current assessment for the provided {@code String} source ID.
     * @param sourceId The source ID of the student being searched.
     * @return The assessment details for the most-recently assigned assessment for this student.
     *  Otherwise, throws a NO_CURRENT_ASSESSMENT exception.
     */
    AssessmentTestDetails getCurrentAssessment(Long sourceId);

    /**
     * Retrieves the current assessment for the provided {@code String} source ID, if it exists.
     * @param sourceId The source ID of the student being searched.
     * @return The assessment details for the most-recently assigned assessment for this student.
     *  Otherwise, returns null.
     */
    AssessmentTestDetails getCurrentAssessmentIfExists(Long sourceId);

    /**
     * Populates the user and assessment details for taking an assessment.
     * @param user The user session being populated.
     */
    void populateSessionAssessment(User user);

    /**
     * Updates the provided assessment with the current session ID.
     *
     * @param sessionId  session identifier
     * @param assessment details to be updated
     */
    void updateAssessmentSession(Long sessionId, AssessmentTestDetails assessment);

    /**
     *
     * @param sourceId The source ID of the student being searched.
     * @param testState A test state being searched.
     * @return A list of applicable tests bearing the searched state.
     */
    List<AssessmentTestDetails> getAssessmentsByUserIdAndTestState(long sourceId, TestState testState);

    /**
     *
     * @param sourceId The source ID of the student being searched.
     * @param testStates A list of test states being searched.
     * @return A list of applicable tests bearing any of the searched states.
     */
    List<AssessmentTestDetails> getAssessmentsByUserIdAndTestStates(long sourceId, List<TestState> testStates);


    void cancelAssessments();
}