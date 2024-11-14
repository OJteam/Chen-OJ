package com.chen.chenoj.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传业务类型枚举
 */
public enum JudgeInfoMessageEnum {

    ACCEPTED("成功", "Accepted"),
    WRONG_ANSWER("答案错误", "Wrong Answer"),
    COMPILE_ERROR("编译错误", "Compile_Error"),
    MEMORY_LIMIT_EXCEEDED("内存溢出", "MEMORY_LIMIT_EXCEEDED"),
    TIME_LIMIT_EXCEEDED("超时", "TIME_LIMIT_EXCEEDED"),
    PRESENTATION_ERROR("展示错误", "PRESENTATION_ERROR"),
    OUTPUT_LIMIT_EXCEEDED("输出溢出", "OUTPUT_LIMIT_EXCEEDED"),
    WAITING("等待中", "WAITING"),
    DANGEROUS_OPERATION("危险操作", "DANGEROUS_OPERATION"),
    RUNTIME_ERROR("运行错误（用户程序的问题）", "RUNTIME_ERROR"),
    SYSTEM_ERROR("系统错误（做系统人的问题）", "SYSTEM_ERROR");

    private final String text;

    private final String value;

    JudgeInfoMessageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static JudgeInfoMessageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeInfoMessageEnum anEnum : JudgeInfoMessageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
