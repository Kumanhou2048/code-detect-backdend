package com.sysu.codeDetect.controller;

import com.sysu.codeDetect.common.BaseResponse;
import com.sysu.codeDetect.common.ErrorCode;
import com.sysu.codeDetect.common.ResultUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

//保存处理文件
@RestController
@RequestMapping("/file")
public class FileController {
    @Value("${file.upload-path}")
    private String savePath;

    //保存文件接口
    @PostMapping("/uploadfile")
    public BaseResponse<String> uploadFile(@RequestParam MultipartFile file) {
        //取得原文件后缀名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //用uuid＋后缀名重新生成文件名
        String newFileName = UUID.randomUUID().toString() + suffix;
        //检查存储目录是否存在
        File dest = new File(new File(savePath), newFileName);
        if(!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        String url = "/file/" + newFileName;
        try {
            file.transferTo(dest);
            //返回图片的相对url地址
            return ResultUtils.success(url);
        }catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ErrorCode.FILE_ERROR);
        }
    }
}
