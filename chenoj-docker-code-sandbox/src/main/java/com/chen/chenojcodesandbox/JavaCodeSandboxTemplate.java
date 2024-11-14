package com.chen.chenojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.chen.chenojcodesandbox.model.ExecuteCodeRequest;
import com.chen.chenojcodesandbox.model.ExecuteCodeResponse;
import com.chen.chenojcodesandbox.model.ExecuteMessage;
import com.chen.chenojcodesandbox.model.JudgeInfo;
import com.chen.chenojcodesandbox.model.enums.JudgeInfoMessageEnum;
import com.chen.chenojcodesandbox.model.enums.QuestionSubmitStatusEnum;
import com.chen.chenojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.chen.chenojcodesandbox.constant.CodeBlackList.SENSITIVE_WORD_LIST;

@Slf4j
public abstract class JavaCodeSandboxTemplate implements CodeSandbox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    /**
     * 代码运行超时时间
     */
    private static final long TIME_OUT = 10000L;
    
    /**
     * Java安全管理器类存放路径
     */
    private static final String SECURITY_MANAGER_PATH;
    
    /**
     * Java安全管理器类名
     */
    private static final String SECURITY_MANAGER_CLASS_NAME = "NowSecurityManager";
    
    /**
     * 使用hutool的工具类，字典树，存放黑名单
     */
    private static final WordTree WORD_TREE;

    static {
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(SENSITIVE_WORD_LIST);
        // 初始安全配置文件路径
        SECURITY_MANAGER_PATH = System.getProperty("user.dir");
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        System.out.println("当前操作系统：" + System.getProperty("os.name").toLowerCase());
        System.out.println("当前代码使用语言：" + language);

//        1. 把用户的代码保存为文件
        File userCodeFile = saveCodeToFile(code);

        //校验代码中的敏感代码
        FoundWord foundWord = WORD_TREE.matchWord(code);
        if (foundWord != null) {
            System.out.println("包含禁止词：" + foundWord.getFoundWord());
            // 返回错误信息
            return new ExecuteCodeResponse("包含禁止词：" + foundWord.getFoundWord(), QuestionSubmitStatusEnum.FAILED.getValue(), null, new JudgeInfo(JudgeInfoMessageEnum.DANGEROUS_OPERATION.getValue(), null, null));
        }

//        2. 编译代码，得到 class 文件
        ExecuteMessage compileFileExecuteMessage = compileFile(userCodeFile);
        System.out.println("编译结果：" + compileFileExecuteMessage);
        
        if (compileFileExecuteMessage.getErrorMessage() != null) {
            // 返回编译错误信息
            return new ExecuteCodeResponse(compileFileExecuteMessage.getMessage(), QuestionSubmitStatusEnum.FAILED.getValue(), null, new JudgeInfo(compileFileExecuteMessage.getErrorMessage(), null, null));
        }

        // 3. 执行代码，得到输出结果
        List<ExecuteMessage> executeMessageList = runFile(userCodeFile, inputList);

//        4. 收集整理输出结果
        ExecuteCodeResponse outputResponse = getOutputResponse(executeMessageList);
//        5. 文件清理
        Boolean b = deleteFile(userCodeFile);
        if(!b) {
            log.error("删除文件失败{}", userCodeFile);
        }
        return outputResponse;

    }

    /**
     * 1. 把用户代码保存为文件
     * @param code 用户代码
     * @return
     */
    public File saveCodeToFile(String code) {
        //        1. 把用户的代码保存为文件

        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 把用户的代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return userCodeFile;
    }

    /**
     * 2. 编译代码
     * @param userCodeFile
     * @return
     */
    public ExecuteMessage compileFile(File userCodeFile) {
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            if (executeMessage.getExitValue() != 0) {
                executeMessage.setExitValue(1);
                executeMessage.setMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getText());
                executeMessage.setErrorMessage(JudgeInfoMessageEnum.COMPILE_ERROR.getValue());
            }
            // 返回执行结果
            return executeMessage;
        } catch (Exception e) {
           // 未知错误
            ExecuteMessage executeMessage = new ExecuteMessage();
            executeMessage.setExitValue(1);
            executeMessage.setMessage(e.getMessage());
            executeMessage.setErrorMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue());
            return executeMessage;
        }
    }

    /**
     * 3. 执行文件，获得执行结果列表
     * @param userCodeFile
     * @param inputList
     * @return
     */
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String input : inputList) {
            String runCmd = String.format("java -Xmx128m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, input);
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("nix") || osName.contains("nux")) {
                runCmd = String.format("java -Xmx128m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, input);
            }
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 超时控制
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        runProcess.destroy();
                        System.out.println("超过程序最大运行时间，终止进程");
                    } catch (InterruptedException e) {
                        System.out.println("结束");
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, input);
                System.out.println("本次运行结果：" + executeMessage);
                if (executeMessage.getExitValue() != 0) {
                    executeMessage.setExitValue(1);
                    executeMessage.setMessage(JudgeInfoMessageEnum.RUNTIME_ERROR.getText());
                    executeMessage.setErrorMessage(JudgeInfoMessageEnum.RUNTIME_ERROR.getValue());
                }
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                // 未知错误
                ExecuteMessage executeMessage = new ExecuteMessage();
                executeMessage.setExitValue(1);
                executeMessage.setMessage(e.getMessage());
                executeMessage.setErrorMessage(JudgeInfoMessageEnum.SYSTEM_ERROR.getValue());
                executeMessageList.add(executeMessage);
            }
        }
        return executeMessageList;
    }

    /**
     * 4. 获取输出结果
     * @param executeMessageList
     * @return
     */
    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        // 取用时最大值，便于判断是否超时
        long maxTime = 0;
        long maxMemory = 0;
        for (ExecuteMessage executeMessage : executeMessageList) {
            String errorMessage = executeMessage.getErrorMessage();
            if (StrUtil.isNotBlank(errorMessage)) {
                //执行中出现错误
                // 用户提交的代码执行中存在错误,直接返回
                executeCodeResponse.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
                executeCodeResponse.setJudgeInfo(new JudgeInfo(errorMessage, null, null));
                break;
            }
            //如果没有错误信息就正常添加
            outputList.add(executeMessage.getMessage());
            Long time = executeMessage.getTime();
            if (time != null) {
                maxTime = Math.max(maxTime, time);

            }
            Long memory = executeMessage.getMemory();
            if (memory != null)
            {
                maxMemory += memory;
            }
        }
        // 正常运行完成
        if (outputList.size() == executeMessageList.size()) {
            executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
            executeCodeResponse.setMessage(QuestionSubmitStatusEnum.SUCCEED.getText());
        }
        executeCodeResponse.setOutputList(outputList);
        //正常运行
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMemory(maxMemory);
        judgeInfo.setTime(maxTime);
        // 运行正常完成则不设置message，交由判题机判题
        executeCodeResponse.setJudgeInfo(judgeInfo);
        System.out.println(executeCodeResponse);
        return executeCodeResponse;
    }

    /**
     * 5. 删除文件
     * @param userCodeFile
     * @return
     */
    public boolean deleteFile(File userCodeFile) {
        if (userCodeFile.getParentFile() != null) {
            String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }

    /**
     * 6. 获取错误响应
     *
     * @param e
     * @return
     */
    private ExecuteCodeResponse getErrorResponse(Throwable e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        // 表示代码沙箱错误
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }
}
