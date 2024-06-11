package com.scholastic.srmapi.service.support;

import com.scholastic.aa.AssessmentAlgorithm;
import com.scholastic.aa.api.IAssessmentAlgorithm;
import com.scholastic.aa.api.enums.Grade;
import com.scholastic.aa.api.enums.TeacherJudgement;
import com.scholastic.srmapi.assessment.callback.LitproUSAssessmentCallback;
import com.scholastic.srmapi.model.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentInterfaceServiceImplTest {

    @Mock
    private LitproUSAssessmentCallback callback;
    @Mock
    private UserSession session;
    @Mock
    private AssessmentInterfaceServiceImpl service;

    @BeforeEach
    void testSetup() {
        ReflectionTestUtils.setField(service, "userSession", session);
        ReflectionTestUtils.setField(service, "litproUSAssessmentCallback", callback);
    }

    @Test
    @DisplayName("Verify assessment detail setup")
    void assessmentSetup() {
        AssessmentInterfaceServiceImpl spy = spy(service);
        IAssessmentAlgorithm algorithm = Mockito.mock(AssessmentAlgorithm.class);
        doReturn(algorithm).when(spy).getIAssessmentAlgorithm();

        when(session.getTeacherJudgement()).thenReturn(TeacherJudgement.ON_LEVEL);
        when(session.getLexileScore()).thenReturn(BigDecimal.ONE);
        spy.prepareAndStartAssessment();

        verify(algorithm, times(1)).setGrade(Grade.GRADE_1);
        verify(algorithm, times(1)).setSchoolYearStart(any());
        verify(algorithm, times(1)).setMeasure(BigDecimal.ONE);
        verify(algorithm, times(1)).setTeacherJudgement(TeacherJudgement.ON_LEVEL);
        verify(algorithm, times(1)).setPractice(true);
        verify(algorithm, times(1)).setPracticeEnabled(true);
        verify(algorithm, times(1)).start();

    }

}
