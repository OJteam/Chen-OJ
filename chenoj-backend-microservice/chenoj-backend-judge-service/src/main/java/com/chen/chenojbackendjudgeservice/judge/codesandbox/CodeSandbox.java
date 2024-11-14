package com.chen.chenojbackendjudgeservice.judge.codesandbox;


import com.chen.chenojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.chen.chenojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
