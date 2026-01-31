package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.utils.JsonResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${upload.urlBase}")
    private String urlBase;

    /**
     * POST /api/upload
     * 接收字段名为 file 的 multipart/form-data 上传（与小程序 wx.uploadFile 对应）
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JsonResult<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return JsonResult.error(400, "图片为空");
        }

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + ext;

        try {
            Path dir = Paths.get(uploadDir);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path target = dir.resolve(filename);
            // 保存到磁盘
            file.transferTo(target.toFile());

            // 构造可访问 URL，确保 urlBase 末尾与 filename 拼接正确
            String fileUrl;
            if (urlBase.endsWith("/")) {
                fileUrl = urlBase + filename;
            } else {
                fileUrl = urlBase + "/" + filename;
            }

            Map<String, String> data = new HashMap<>();
            data.put("url", fileUrl);
            return JsonResult.success(data);
        } catch (IOException e) {
            e.printStackTrace();
            return JsonResult.error(500, "保存失败");
        }
    }
}