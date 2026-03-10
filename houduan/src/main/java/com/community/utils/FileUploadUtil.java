package com.community.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Component
public class FileUploadUtil {

    @Value("${file.upload.path:${user.dir}/uploads/}")
    private String uploadPath;

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 文件访问路径
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 获取文件扩展名
        String extension = FileUtil.extName(originalFilename);
        if (StrUtil.isBlank(extension)) {
            throw new IllegalArgumentException("不支持的文件格式");
        }

        // 检查文件类型
        if (!isValidFileType(extension)) {
            throw new IllegalArgumentException("不支持的文件格式: " + extension);
        }

        // 生成新的文件名
        String newFileName = IdUtil.simpleUUID() + "." + extension;

        // 按日期创建子目录
        String dateDir = LocalDate.now().toString().replace("-", "/");
        String relativePath = "/files/" + dateDir + "/" + newFileName;

        // 创建上传目录
        String fullPath = uploadPath + relativePath;
        FileUtil.mkParentDirs(fullPath);

        // 保存文件
        file.transferTo(new java.io.File(fullPath));

        return relativePath;
    }

    /**
     * 检查文件类型是否合法
     *
     * @param extension 文件扩展名
     * @return 是否合法
     */
    private boolean isValidFileType(String extension) {
        String[] allowedTypes = {"jpg", "jpeg", "png", "gif", "bmp", "pdf", "doc", "docx", "xls", "xlsx", "txt"};
        for (String type : allowedTypes) {
            if (type.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}