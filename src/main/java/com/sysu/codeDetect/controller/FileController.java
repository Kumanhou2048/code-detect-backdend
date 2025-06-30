package com.sysu.codeDetect.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sysu.codeDetect.common.BaseResponse;
import com.sysu.codeDetect.common.ErrorCode;
import com.sysu.codeDetect.common.ResultUtils;
import com.sysu.codeDetect.service.UserService;
import com.sysu.codeDetect.model.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.UUID;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

//保存处理文件
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload-path}")
    private String savePath;

    @Resource
    private UserService usersService;

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

    @PostMapping("/uploadAvatar")
    public String uploadAvatar(@RequestParam MultipartFile file, @RequestParam String userAccount) {
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

        // 查询旧头像的 URL
        // 假设有一个 QueryWrapper 对象，设置查询条件为 name = 'John Doe'
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User user = usersService.getOne(queryWrapper); // 调用 getOne 方法
        String oldAvatarUrl = user.getAvatarUrl();

        // 删除本地旧头像文件
        if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty() && !"/file/Kuman.png".equals(oldAvatarUrl)) {
            String oldFilePath = savePath + "/" + oldAvatarUrl.replace("/file/", "");
            File oldFile = new File(oldFilePath);
            if (oldFile.exists() && oldFile.isFile()) {
                if (!oldFile.delete()) {
                    log.print("Failed to delete old avatar file: " + oldFilePath);
                }
            }
        }

        //更新数据库
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userAccount", userAccount).set("avatarUrl", "/file/" + newFileName);
        usersService.update(updateWrapper); // 调用 update 方法

        try {
            file.transferTo(dest);
            //返回图片的相对url地址
            return "/file/" + newFileName;
        }catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
    }
}
