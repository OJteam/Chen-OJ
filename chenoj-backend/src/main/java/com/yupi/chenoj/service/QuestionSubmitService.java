package com.yupi.chenoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.chenoj.model.dto.question.QuestionQueryRequest;
import com.yupi.chenoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yupi.chenoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yupi.chenoj.model.entity.Question;
import com.yupi.chenoj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.chenoj.model.entity.User;
import com.yupi.chenoj.model.vo.QuestionSubmitVO;
import com.yupi.chenoj.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author chen
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-09-04 17:43:17
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest 题目提交信息
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);
}
