package com.community.controller;

import com.community.common.Result;
import com.community.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @PostMapping("/image")
    public Result<String> uploadImage(MultipartFile file) {
        try {
            String filePath = fileUploadUtil.uploadFile(file);
            return Result.success(filePath);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/file")
    public Result<String> uploadFile(MultipartFile file) {
        try {
            String filePath = fileUploadUtil.uploadFile(file);
            return Result.success(filePath);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}