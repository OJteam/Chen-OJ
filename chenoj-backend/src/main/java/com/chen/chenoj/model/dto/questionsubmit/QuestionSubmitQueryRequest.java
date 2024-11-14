package com.chen.chenoj.model.dto.questionsubmit;

import com.chen.chenoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 提交状态
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 题目 id(string)
     */
    private String questionIdStr;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户 账号
     */
    private String userAccount;

    private static final long serialVersionUID = 1L;
}