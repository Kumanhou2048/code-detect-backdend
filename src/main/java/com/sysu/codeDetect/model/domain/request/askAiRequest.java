package com.sysu.codeDetect.model.domain.request;

import lombok.Data;
import java.io.Serializable;

@Data
public class askAiRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120796L;

    //需要优化的代码文件的相对url
    private String sourceFileUrl;

    //代码语言类型
    private String languageType;

    //目标大模型
    private String model;

    //当前用户的账号
    private String userAccount;
}
