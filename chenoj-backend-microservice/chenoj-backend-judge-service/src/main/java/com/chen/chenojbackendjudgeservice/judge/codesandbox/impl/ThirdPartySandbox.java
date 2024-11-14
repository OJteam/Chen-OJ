package com.chen.chenojbackendjudgeservice.judge.codesandbox.impl;


import com.chen.chenojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.chen.chenojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.chen.chenojbackendmodel.model.codesandbox.ExecuteCodeResponse;

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
