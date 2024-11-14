package com.chen.chenojcodesandbox;

import com.chen.chenojcodesandbox.model.ExecuteCodeRequest;
import com.chen.chenojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Java 原生代码沙箱实现（直接复用模版方法）
 */
@Component
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate {


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
