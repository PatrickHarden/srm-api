package com.scholastic.srmapi.repository;

import com.scholastic.srmapi.model.AssessmentTestDetails;
import com.scholastic.srmapi.util.Constants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AssessmentTestRepository extends JpaRepository<AssessmentTestDetails, Integer> {

    /**
     * Find all assessments for a given source ID, which are in one of potentially multiple test states,
     *  with a creation date within the past two weeks, in order of most recently created.
     * @param sourceId The {@code String} source ID which will be searched.
     * @param testStates The list of applicable {@code TestState} which the assessment can be under
     * @return All assessments that fall under the above criteria in an {@code AssessmentTestDetails}
     */
    @Query(nativeQuery = true,
            value ="""
            SELECT * FROM assessment_test assessment
            WHERE assessment.source_id = :sourceId
            AND assessment.test_state IN :testStates"""
                    + Constants.TWO_WEEKS_TESTS + " ORDER BY assessment.created_date DESC")
    List<AssessmentTestDetails> findCurrentBySourceIdAndTestStates(long sourceId, List<String> testStates);


    /**
     * Find all assessments for a given source ID, which are in one of potentially multiple test states,
     *  in order of most recently created.
     * @param sourceId The {@code String} source ID which will be searched.
     * @param testStates The list of applicable {@code TestState} which the assessment can be under
     * @return All assessments that fall under the above criteria in an {@code AssessmentTestDetails}
     */
    @Query(nativeQuery = true,
            value ="""
            SELECT * FROM assessment_test assessment
            WHERE assessment.source_id = :sourceId
            AND assessment.test_state IN :testStates"""
                   + " ORDER BY assessment.created_date DESC")
    List<AssessmentTestDetails> findCurrentBySourceIdAndTestStatesNoRestrictions(long sourceId, List<String> testStates);

    @Modifying
    @Query(nativeQuery = true,
            value ="""      
                    UPDATE assessment_test assessment
                     SET assessment.test_state = 'CANCELLED', assessment.modified_date = :currentInstant
                     WHERE assessment.test_state IN ("IN_PROGRESS", "PRACTICE")
                     AND datediff( :currentInstant , assessment.created_date) >= 14;""")
    void cancelOutdatedAssessmentsInProgress(Instant currentInstant);

    @Query(nativeQuery = true,
            value = """
                    SELECT * FROM assessment_test assessment
                     WHERE assessment.test_state = 'CANCELLED' AND assessment.modified_date >= :currentInstant""")
    List<AssessmentTestDetails> getRecentlyCancelledAssessments(Instant currentInstant);

}
