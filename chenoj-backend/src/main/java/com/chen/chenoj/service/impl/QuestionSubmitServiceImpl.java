package com.chen.chenoj.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.chenoj.exception.BusinessException;
import com.chen.chenoj.judge.JudgeService;
import com.chen.chenoj.mapper.QuestionSubmitMapper;
import com.chen.chenoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.chen.chenoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.chen.chenoj.model.entity.Question;
import com.chen.chenoj.model.entity.QuestionSubmit;
import com.chen.chenoj.model.entity.User;
import com.chen.chenoj.model.enums.QuestionSubmitLanguageEnum;
import com.chen.chenoj.model.enums.QuestionSubmitStatusEnum;
import com.chen.chenoj.model.vo.QuestionSubmitVO;
import com.chen.chenoj.model.vo.QuestionVO;
import com.chen.chenoj.service.QuestionService;
import com.chen.chenoj.service.QuestionSubmitService;
import com.chen.chenoj.service.UserService;
import com.chen.chenoj.utils.SqlUtils;
import com.chen.chenoj.common.ErrorCode;
import com.chen.chenoj.constant.CommonConstant;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author chen
* @description 针对表【question_submit(提交题目)】的数据库操作Service实现
* @createDate 2024-09-04 17:43:17
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        //校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if(languageEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        String userAccount = loginUser.getUserAccount();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setUserAccount(userAccount);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        //设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        //执行判题服务
        CompletableFuture.runAsync(() -> {
            judgeService.doJudge(questionSubmitId);
        });
        return questionSubmitId;
    }


    /**
     * 获取查询包装类（用户根据哪个字段查询，根据前端传来的请求对象，得到mybatis框架支持的查询 QueryWrapper 类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        //String questionIdStr = String.valueOf(questionSubmitQueryRequest.getQuestionId()); // 默认值
        Long userId = questionSubmitQueryRequest.getUserId();
        String userAccount = questionSubmitQueryRequest.getUserAccount();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();


        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        //queryWrapper.eq(ObjectUtils.isNotEmpty(questionIdStr), "questionIdStr", questionIdStr);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userAccount), "userAccount", userAccount);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        Question question = questionService.getById(questionSubmit.getQuestionId());
        questionSubmitVO.setQuestionVO(QuestionVO.objToVo(question));
        questionSubmitVO.setUserVO(userService.getUserVO(loginUser));
        //脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同） 提交的代码
        long userId = loginUser.getId();
        //处理脱敏
        if(userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)){
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    @Override
    public List<QuestionSubmitVO> getQuestionSubmitVOList(List<QuestionSubmit> questionSubmitList, User loginUser) {
        ArrayList<QuestionSubmitVO> questionSubmitListVo = new ArrayList<>();
        questionSubmitList.forEach(questionSubmit -> {
            QuestionSubmitVO questionSubmitVO = getQuestionSubmitVO(questionSubmit, loginUser);
            questionSubmitListVo.add(questionSubmitVO);
        });
        Collections.reverse(questionSubmitListVo);
        return questionSubmitListVo;
    }

}




