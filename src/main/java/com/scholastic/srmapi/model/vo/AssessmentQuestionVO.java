package com.scholastic.srmapi.model.vo;

import com.scholastic.aa.api.IAssessmentQuestion;
import com.scholastic.srmapi.model.AssessmentQuestion;
import lombok.Getter;

@Getter
public class AssessmentQuestionVO implements IAssessmentQuestion {

    private static final String US_ZONE_CODE = "US";

    private String answer2;
    private String answer3;
    private String answer4;
    private String answerCorrect;
    private int measure;
    private String passage;
    private String pool;
    private String question;
    private String bank;
    private String id;

    public static AssessmentQuestionVO build(AssessmentQuestion assessmentQuestion) {
        var assessmentQuestionVO = new AssessmentQuestionVO();
        assessmentQuestionVO.answer2 = assessmentQuestion.getAnswer2();
        assessmentQuestionVO.answer3 = assessmentQuestion.getAnswer3();
        assessmentQuestionVO.answer4 = assessmentQuestion.getAnswer4();
        assessmentQuestionVO.answerCorrect = assessmentQuestion.getAnswerCorrect();
        assessmentQuestionVO.id = String.valueOf(assessmentQuestion.getId());
        assessmentQuestionVO.passage = assessmentQuestion.getPassage();
        assessmentQuestionVO.pool = assessmentQuestion.getPool();
        assessmentQuestionVO.question = assessmentQuestion.getQuestion();
        assessmentQuestionVO.bank = US_ZONE_CODE;
        assessmentQuestionVO.measure = (int) assessmentQuestion.getLexile();
        return assessmentQuestionVO;
    }

}
