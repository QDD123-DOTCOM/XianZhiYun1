package com.example.xianzhiyun.controller;

import com.example.xianzhiyun.entity.UserInfo;
import com.example.xianzhiyun.service.UserInfoService;
import com.example.xianzhiyun.utils.JsonResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/user")
public class UserAvatarController {

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${upload.urlBase}")
    private String urlBase;

    private final UserInfoService userInfoService;

    public UserAvatarController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    /**
     * 修复 401：使用 @RequestAttribute("userId") 替代 Header
     */
    @PutMapping("/avatar")
    public JsonResult<Void> updateAvatar(@RequestBody AvatarUpdateRequest req,
                                         @RequestAttribute("userId") Long userId) {
        if (req == null || req.getAvatarUrl() == null || req.getAvatarUrl().trim().isEmpty()) {
            return JsonResult.error(400, "avatarUrl 不能为空");
        }

        // userId 直接由 JwtFilter 解析并注入，无需手动解析 Header

        String incoming = req.getAvatarUrl().trim();
        String toSave;
        if (incoming.startsWith("http://") || incoming.startsWith("https://")) {
            toSave = incoming;
        } else {
            String filename = extractFilenameFromUrl(incoming);
            if (filename == null || filename.trim().isEmpty()) {
                return JsonResult.error(400, "无法解析上传文件名");
            }
            toSave = urlBase.endsWith("/") ? urlBase + filename : urlBase + "/" + filename;
        }

        // 获取用户并更新
        UserInfo user = userInfoService.getProfile(userId);
        if (user == null) {
            return JsonResult.error(404, "用户不存在");
        }
        String oldAvatarUrl = user.getAvatarUrl();

        if (toSave.equals(oldAvatarUrl)) {
            return JsonResult.success(null);
        }

        user.setAvatarUrl(toSave);
        try {
            userInfoService.updateProfile(user);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.error(500, "更新用户头像失败");
        }

        // 删除旧文件（若位于 upload.dir 下）
        try {
            if (oldAvatarUrl != null && !oldAvatarUrl.trim().isEmpty()) {
                userInfoService.deleteLocalUploadFileIfExists(oldAvatarUrl);
            }
        } catch (Exception e) {
            // 记录但不影响返回
            e.printStackTrace();
        }

        return JsonResult.success(null);
    }

    private String extractFilenameFromUrl(String url) {
        if (url == null) return null;
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            if (path == null) return null;
            int idx = path.lastIndexOf('/');
            if (idx >= 0 && idx < path.length() - 1) return path.substring(idx + 1);
            return null;
        } catch (URISyntaxException e) {
            int idx = url.lastIndexOf('/');
            if (idx >= 0 && idx < url.length() - 1) return url.substring(idx + 1);
            return null;
        }
    }

    public static class AvatarUpdateRequest {
        private String avatarUrl;
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }
}