package com.scholastic.srmapi.service;


import com.scholastic.aa.api.IAssessmentAlgorithm;

/**
 * Service for interacting with the Assessment Algorithm leveraged by the SRM.
 */
public interface AssessmentInterfaceService {

    /**
     * Fetch the algorithm being used by the interface.
     * @return The leveraged algorithm
     */
    IAssessmentAlgorithm getIAssessmentAlgorithm();

    /**
     * Prepare algorithm and start assessment.
     */
    void prepareAndStartAssessment();

}