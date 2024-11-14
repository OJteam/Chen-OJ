package com.chen.chenoj.judge.codesandbox.impl;

import com.chen.chenoj.judge.codesandbox.CodeSandbox;
import com.chen.chenoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.chen.chenoj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 第三方代码沙箱（调用网上现成的代码沙箱）
 */
public class ThirdPartySandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
