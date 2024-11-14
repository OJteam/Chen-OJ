package com.chen.chenojbackendjudgeservice.judge.codesandbox;


import com.chen.chenojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandbox;
import com.chen.chenojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandbox;
import com.chen.chenojbackendjudgeservice.judge.codesandbox.impl.ThirdPartySandbox;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 */
public class CodeSandboxFactory {

    /**
     * 创建代码沙箱实例
     * @param type 沙箱类型
     * @return
     */
    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartySandbox();
            default:
                return  new ExampleCodeSandbox();
        }
    }
}
