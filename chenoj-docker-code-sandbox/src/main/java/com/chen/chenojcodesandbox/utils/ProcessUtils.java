package com.chen.chenojcodesandbox.utils;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import com.chen.chenojcodesandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 进程工具类
 */
@Slf4j
public class ProcessUtils {

    /**
     * 执行进程并获取信息
     * @param runProcess
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        //记录程序还未执行的内存使用量
        long initialMemory = getUsedMemory();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            //等待程序执行获取错误码
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            //正常退出
            if (exitValue == 0) {
                System.out.println(opName + "成功");
                //运行正常输出流
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), StandardCharsets.UTF_8));
                List<String> outputStrList = new ArrayList<>();
                //进行逐行读取
                String complieOutLine;
                while ((complieOutLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(complieOutLine);
                }
                executeMessage.setMessage(StringUtils.join(outputStrList, '\n'));
            } else {
                //异常退出
                System.out.println(opName + "失败：错误码：" + exitValue);
                //运行正常输出流
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), StandardCharsets.UTF_8));
                List<String> outputStrList = new ArrayList<>();
                //进行逐行读取
                String complieOutLine;
                while ((complieOutLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(complieOutLine);
                }
                executeMessage.setMessage(StringUtils.join(outputStrList, '\n'));
                //分批获取错误输出
                BufferedReader bufferedReaderError = new BufferedReader(new InputStreamReader(runProcess.getErrorStream(), StandardCharsets.UTF_8));
                //逐行读取
                List<String> errorOutputStrList = new ArrayList<>();
                String complieOutLineError;
                while ((complieOutLineError = bufferedReaderError.readLine()) != null) {
                    errorOutputStrList.add(complieOutLineError);
                }
                executeMessage.setErrorMessage(StringUtils.join(errorOutputStrList, '\n'));
            }
            long finalMemory = getUsedMemory();
            // 计算内存使用量，单位字节，转换成kb需要除以1024
            long memoryUsage = finalMemory - initialMemory;
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getTotalTimeMillis());
            executeMessage.setMemory(memoryUsage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return executeMessage;
    }

    /**
     * 执行交互式进程并获取信息
     * @param runProcess
     * @return
     */
    public static ExecuteMessage runInteractProcessAndGetMessage(Process runProcess, String args) throws IOException {
        // 向控制台输入程序
        ExecuteMessage executeMessage = new ExecuteMessage();
        //记录程序还未执行的内存使用量
        long initialMemory = getUsedMemory();
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(runProcess.getOutputStream())) {
            String[] arguments = args.split(" ");
            for (String arg : arguments) {
                outputStreamWriter.write(arg);
                outputStreamWriter.write("\n");
            }
            // 相当于按了回车，执行输入的发送
            outputStreamWriter.flush();
            //记录程序开始执行时间
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            int exitValue = runProcess.waitFor();
            stopWatch.stop();
            executeMessage.setExitValue(exitValue);
            if (exitValue == 0) {
                // 分批获取进程的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                List<String> outputStrList = new ArrayList<>();
                // 逐行读取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(compileOutputLine);
                }
                executeMessage.setMessage(StringUtils.join(outputStrList, '\n'));
            } else {
                //异常退出
                System.out.println("失败：错误码：" + exitValue);
                //运行正常输出流
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), StandardCharsets.UTF_8));
                List<String> outputStrList = new ArrayList<>();
                //进行逐行读取
                String complieOutLine;
                while ((complieOutLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(complieOutLine);
                }
                executeMessage.setErrorMessage(StringUtils.join(outputStrList, '\n'));
                //分批获取错误输出
                BufferedReader bufferedReaderError = new BufferedReader(new InputStreamReader(runProcess.getErrorStream(), StandardCharsets.UTF_8));
                //逐行读取
                List<String> errorOutputStrList = new ArrayList<>();
                String complieOutLineError;
                while ((complieOutLineError = bufferedReaderError.readLine()) != null) {
                    errorOutputStrList.add(complieOutLineError);
                }
                executeMessage.setErrorMessage(StringUtils.join(errorOutputStrList, '\n'));

            }
            long finalMemory = getUsedMemory();
            // 计算内存使用量，单位字节，转换成kb需要除以1024
            long memoryUsage = finalMemory - initialMemory;
            executeMessage.setTime(stopWatch.getTotalTimeMillis());
            executeMessage.setMemory(memoryUsage);
        } catch (Exception e) {
            // 使用日志框架记录异常
            log.error("执行交互式进程出错", e);
        } finally {
            // 记得资源的释放，否则会卡死
            runProcess.destroy();
        }
        return executeMessage;
    }

    public static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
