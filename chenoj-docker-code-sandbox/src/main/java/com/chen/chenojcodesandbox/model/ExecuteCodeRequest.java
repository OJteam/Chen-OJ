package com.chen.chenojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {

    /**
     * 判题配置
     */
    private List<String> inputList;

    /**
     * 判题代码
     */
    private String code;

    /**
     * 判题使用的语言
     */
    private String language;
}
