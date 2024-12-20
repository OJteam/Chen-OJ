package com.chen.chenojbackendjudgeservice.judge;


import com.chen.chenojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.chen.chenojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.chen.chenojbackendjudgeservice.judge.strategy.JudgeContext;
import com.chen.chenojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.chen.chenojbackendmodel.model.codesandbox.JudgeInfo;
import com.chen.chenojbackendmodel.model.entity.QuestionSubmit;
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
