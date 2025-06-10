package com.sysu.codeDetect.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sysu.codeDetect.common.ErrorCode;
import com.sysu.codeDetect.exception.BusinessException;
import com.sysu.codeDetect.mapper.OptimizeRecordMapper;
import com.sysu.codeDetect.model.domain.OptimizeRecord;
import com.sysu.codeDetect.service.OptimizeRecordService;
import com.sysu.codeDetect.manager.AiManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

/**
* @author 陈誉文
* @description 对于AI大模型调用的相关实现，并且记录用户的历史调用
* @createDate 2025-06-5 13:39:05
*/
@Service
@Slf4j
public class OptimizeRecordServiceImpl extends ServiceImpl<OptimizeRecordMapper, OptimizeRecord>
    implements OptimizeRecordService{

    @Value("${file.upload-path}")
    private String savePath;

    @Resource
    private OptimizeRecordMapper optimizeRecordMapper;

    @Resource
    private AiManager aiManager;

    // 提取函数
    private String extractBetween(String text, String start, String end) {
        int startIdx = text.indexOf(start);
        int endIdx = text.indexOf(end);
        if (startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
            return text.substring(startIdx + start.length(), endIdx).trim();
        }
        return "";
    }

    /**
     * 用将前端的请求发送给大模型，要求大模型回复
     *
     * @param sourceFileUrl  需要优化的代码文件的相对url
     * @param languageType 代码语言类型
     * @param model 目标大模型
     * @param userAccount 当前用户的账号
     * @return 优化后的代码文件的url
     */
    @Override
    public String askAI(String sourceFileUrl, String languageType, String model, String userAccount) {
        // 拼接磁盘完整路径，去除重复 file
        String cleanUrl = sourceFileUrl.replaceFirst("^/file", ""); // "/test.cpp"
        String codeFileFullUrl = savePath + cleanUrl; // "D:/.../file/test.cpp"
        // 读取代码内容
        StringBuilder codeBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(codeFileFullUrl))) {
            String line;
            while ((line = reader.readLine()) != null) {
                codeBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取代码文件失败: " + e.getMessage());
        }
        // 获取文件名和后缀
        String fileName = new File(codeFileFullUrl).getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".")); // 例：.cpp
        // 构造提示词
        String systemPrompt = "你是一个经验丰富的程序优化专家。";
        String userPrompt = "请对下面的源代码进行深入优化，并返回两个部分的内容：\n" +
                "1. 优化后的完整源代码（保持原语言格式），请使用如下标记包裹：\n" +
                "=== 优化后代码开始 ===\n" +
                "...\n" +
                "=== 优化后代码结束 ===\n\n" +
                "2. 一份结构化的优化报告，请使用如下标记包裹：\n" +
                "=== 优化报告开始 ===\n" +
                "...\n" +
                "=== 优化报告结束 ===\n\n" +
                "代码优化报告模板如下，请严格遵循格式生成内容：\n\n" +
                "代码优化报告\n" +
                "文件名称：" + fileName + "\n" +
                "分析时间：" + LocalDate.now() + "\n" +
                "模型版本：" + model + "\n\n" +
                "一、概览总结\n" +
                "优化类型\t问题数\t说明\n" +
                "命名规范问题\tX\t包含变量、函数、类命名不规范等\n" +
                "性能瓶颈\tX\t存在低效算法、冗余循环等问题\n" +
                "结构重构建议\tX\t存在重复代码、函数职责不清等\n" +
                "其他建议\tX\t包含风格统一、注释优化等\n\n" +
                "二、详细优化建议\n" +
                "优化项1：命名优化\n" +
                "问题位置：/path/to/file.cpp: line 42\n" +
                "问题描述：变量名 a 命名不明确，难以体现其在上下文中的含义。\n" +
                "优化建议：将变量名 a 改为 user_list，使其语义清晰，便于阅读和维护。\n" +
                "修改示例：\n" +
                "- a = get_all_users()\n" +
                "+ user_list = get_all_users()\n\n" +
                "请开始分析并返回以下源代码的优化结果。\n\n" +
                "源代码(你需要进行优化的代码)如下：\n" +
                "```\n" +
                codeBuilder +
                "```\n";

        //调用AiManager
        String aiResponse = aiManager.doChat(systemPrompt, userPrompt, model);
        // 提取优化后的代码
        String optimizedCode = extractBetween(aiResponse, "=== 优化后代码开始 ===", "=== 优化后代码结束 ===");
        // 提取优化报告
        String optimizeReport = extractBetween(aiResponse, "=== 优化报告开始 ===", "=== 优化报告结束 ===");
        // 生成优化后代码文件名
        String codeFileName = UUID.randomUUID().toString() + "_optimized" + suffix;
        String reportFileName = UUID.randomUUID().toString() + "_report.txt";

        File codeFile = new File(savePath, codeFileName);
        File reportFile = new File(savePath, reportFileName);

        // 确保目录存在
        if (!codeFile.getParentFile().exists()) {
            codeFile.getParentFile().mkdirs();
        }

        try {
            // 写入优化后的代码
            try (BufferedWriter codeWriter = new BufferedWriter(new FileWriter(codeFile))) {
                codeWriter.write(optimizedCode);
            }
            // 写入优化报告
            try (BufferedWriter reportWriter = new BufferedWriter(new FileWriter(reportFile))) {
                reportWriter.write(optimizeReport);
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_ERROR, "保存文件失败: " + e.getMessage());
        }

        // 返回两个相对URL地址，格式如：/file/xxx.cpp
        String codeUrl = "/file/" + codeFileName;
        String reportUrl = "/file/" + reportFileName;
        //将调用历史保存在数据库中
        OptimizeRecord optimizeRecord = new OptimizeRecord();
        optimizeRecord.setUserAccount(userAccount);
        optimizeRecord.setOriginalCodeFileUrl(sourceFileUrl);
        optimizeRecord.setOptimizedCodeFileUrl(codeUrl);
        optimizeRecord.setReportUrl(reportUrl);
        optimizeRecord.setCreateTime(new Date());
        optimizeRecord.setUpdateTime(new Date());
        boolean saveResult = this.save(optimizeRecord);
        if(!saveResult) {
            return null;
        }
        return codeUrl;
    }
}




