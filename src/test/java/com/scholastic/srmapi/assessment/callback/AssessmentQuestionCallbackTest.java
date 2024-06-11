package com.scholastic.srmapi.assessment.callback;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * To be re-populated with Callback implementation.
 */
class AssessmentQuestionCallbackTest {

    private final AssessmentQuestionCallback callback = new AssessmentQuestionCallback();
    @Test
    void testCalled() {
        assertThat(callback.getRandomQuestion("pool", 0, 0)).isNotNull();
        assertThat(callback.getRandomQuestionInRange("pool", 0, 0)).isNotNull();
    }
}
