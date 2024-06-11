package com.scholastic.srmapi.assessment.callback;

import com.scholastic.aa.api.IAssessmentQuestion;
import com.scholastic.srmapi.model.AssessmentQuestion;
import com.scholastic.srmapi.model.vo.AssessmentQuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AssessmentQuestionCallback {

    //The top ten questions is the documented number to randomize from
    public IAssessmentQuestion getRandomQuestion(String pool, int scoreLow, int scoreHigh) {
        // To be populated
        log.warn("Get random question called on pool {}, scoreLow {}, scoreHigh {}", pool, scoreLow, scoreHigh);
        return AssessmentQuestionVO.build(buildTempAssessmentQuestion());
    }

    public IAssessmentQuestion getRandomQuestionInRange(String pool, int range, double score) {
        // To be populated
        log.warn("Get random question in range called on pool {}, range {}, score {}", pool, range, score);
        return AssessmentQuestionVO.build(buildTempAssessmentQuestion());
    }

    private AssessmentQuestion buildTempAssessmentQuestion() {
        var assessmentQuestion = new AssessmentQuestion();
        assessmentQuestion.setId(1);
        assessmentQuestion.setQuestion("Dummy Question");
        assessmentQuestion.setPassage("This is a test question with test passage info");
        assessmentQuestion.setCredit("10");
        assessmentQuestion.setPool("1");
        assessmentQuestion.setLexile(10L);
        assessmentQuestion.setActive(true);
        assessmentQuestion.setAnswerCorrect("correct");
        assessmentQuestion.setAnswer2("ans2");
        assessmentQuestion.setAnswer3("ans3");
        assessmentQuestion.setAnswer4("ans4");

        return assessmentQuestion;
    }
}
