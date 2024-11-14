package com.chen.chenojcodesandbox;

import com.chen.chenojcodesandbox.model.ExecuteCodeRequest;
import com.chen.chenojcodesandbox.model.ExecuteCodeResponse;

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
