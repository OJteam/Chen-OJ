package com.chen.chenoj.judge;

import com.chen.chenoj.judge.codesandbox.model.JudgeInfo;
import com.chen.chenoj.judge.strategy.DefaultJudgeStrategy;
import com.chen.chenoj.judge.strategy.JavaLanguageJudgeStrategy;
import com.chen.chenoj.judge.strategy.JudgeContext;
import com.chen.chenoj.judge.strategy.JudgeStrategy;
import com.chen.chenoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if("java".equals(language)){
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
