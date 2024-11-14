package com.chen.chenojcodesandbox.model;

import lombok.Data;

/**
 * 进程执行信息
 */
@Data
public class ExecuteMessage {

    /**
     * 退出码
     */
    private Integer exitValue;

    /**
     * 正常输出信息
     */
    private String message;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 程序执行时间
     */
    private  Long time;

    /**
     * 执行内存
     */
    private Long memory;
}
