package com.sysu.codeDetect.controller;

import com.sysu.codeDetect.common.BaseResponse;
import com.sysu.codeDetect.common.ErrorCode;
import com.sysu.codeDetect.common.ResultUtils;
import com.sysu.codeDetect.exception.BusinessException;
import com.sysu.codeDetect.model.domain.request.askAiRequest;
import com.sysu.codeDetect.model.domain.request.showrecordRequest;
import com.sysu.codeDetect.service.OptimizeRecordService;
import com.sysu.codeDetect.service.impl.OptimizeRecordServiceImpl;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

//大模型调用相关接口
@RestController
@RequestMapping("/Ai")
public class AiController {

    @Resource
    private OptimizeRecordService optimizeRecordService;

    @PostMapping("/askAi")
    public BaseResponse<String> askAi (@RequestBody askAiRequest request) {
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //需要优化的代码文件的相对url
        String sourceFileUrl = request.getSourceFileUrl();
        //用户上传代码文件的文件名
        String sourceFileName = request.getSourceFileName();
        //代码语言类型
        String languageType = request.getLanguageType();
        //目标大模型
        String model = request.getModel();
        //当前用户的账号
        String userAccount = request.getUserAccount();
        if(StringUtils.isAnyBlank(sourceFileUrl,sourceFileName,languageType,model,userAccount)){
            return null;
        }
        String optimizedCodeFileUrl = optimizeRecordService.askAI(sourceFileUrl,sourceFileName,languageType,model,userAccount);
        if (optimizedCodeFileUrl != null){
            return ResultUtils.success(optimizedCodeFileUrl);
        }
        else{
            return ResultUtils.error(ErrorCode.AI_ERROR);
        }
    }


    @PostMapping("/showrecord")
    public BaseResponse<List<OptimizeRecordServiceImpl.optRecord>> showRecord(@RequestBody showrecordRequest scRequest) {
        return optimizeRecordService.getOptimizeRecordList(scRequest.getUserAccount());
    }

    @PostMapping("/showselectrecord")
    public BaseResponse<List<OptimizeRecordServiceImpl.optRecord>> showSelectRecord(@RequestBody showrecordRequest scRequest) {
        return  optimizeRecordService.getSelectOptimizeRecordList(scRequest.getUserAccount(), scRequest.getFileName(), scRequest.getDate());
    }
}
