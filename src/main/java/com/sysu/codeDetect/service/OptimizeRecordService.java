package com.sysu.codeDetect.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sysu.codeDetect.common.BaseResponse;
import com.sysu.codeDetect.model.domain.OptimizeRecord;
import com.sysu.codeDetect.service.impl.OptimizeRecordServiceImpl;

import java.util.List;

/**
 * @author 陈誉文
 * @description 针对表【optimize_record(代码优化记录表)】的数据库操作Service
 * @createDate 2025-06-10 13:39:05
 */
public interface OptimizeRecordService extends IService<OptimizeRecord> {

    /**
     * 用将前端的请求发送给大模型，要求大模型回复
     *
     * @param sourceFileUrl  需要优化的代码文件的相对url
     * @param sourceFileName 用户上传代码的原文件名
     * @param languageType 代码语言类型
     * @param model 目标大模型
     * @param userAccount 当前用户的账号
     * @return 优化后的代码文件的url
     */
    String askAI(String sourceFileUrl, String sourceFileName,String languageType, String model, String userAccount);

    BaseResponse<List<OptimizeRecordServiceImpl.optRecord>> getOptimizeRecordList(String userAccount);

    BaseResponse<List<OptimizeRecordServiceImpl.optRecord>> getSelectOptimizeRecordList(String userAccount, String fileName, String date);
}
