package com.example.xianzhiyun.service.impl;

import com.example.xianzhiyun.entity.UserInfo;
import com.example.xianzhiyun.mapper.UserInfoMapper;
import com.example.xianzhiyun.service.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoMapper userInfoMapper;

    public UserInfoServiceImpl(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    @Value("${upload.dir:upload}")
    private String uploadDir;

    @Override
    public List<UserInfo> listAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public UserInfo getById(Long id) {
        return userInfoMapper.findById(id);
    }

    @Override
    public UserInfo create(UserInfo user) {
        userInfoMapper.insert(user);
        return user;
    }

    @Override
    public List<UserInfo> findByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userInfoMapper.selectAll();
        } else {
            String kw = "%" + keyword.trim() + "%";
            return userInfoMapper.selectByOpenidOrNickname(kw);
        }
    }

    @Override
    public UserInfo update(UserInfo user) {
        userInfoMapper.updateSelective(user);
        return user;
    }

    @Override
    public void delete(Long id) {
        userInfoMapper.deleteById(id);
    }

    @Override
    public UserInfo getProfile(Long id) {
        return userInfoMapper.findById(id);
    }

    @Override
    @Transactional
    public void updateProfile(UserInfo user) {
        userInfoMapper.updateSelective(user);
    }

    @Override
    public int getPoints(Long id) {
        Integer v = userInfoMapper.selectCreditScoreById(id);
        return v == null ? 0 : v;
    }

    @Override
    public int getFavoritesCount(Long id) {
        return 0;
    }

    @Override
    public int getActivitiesCount(Long id) {
        return 0;
    }

    @Override
    public void deleteLocalUploadFileIfExists(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return;
        }
        try {
            String filename = extractFilenameFromUrl(fileUrl);
            if (filename == null || filename.trim().isEmpty()) {
                return;
            }
            File oldFile = Paths.get(uploadDir, filename).toFile();
            if (oldFile.exists() && oldFile.isFile()) {
                boolean deleted = oldFile.delete();
                if (!deleted) {
                    System.err.println("Failed to delete old avatar: " + oldFile.getAbsolutePath());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public UserInfo getByOpenid(String openid) {
        if (openid == null) return null;
        return userInfoMapper.findByOpenid(openid);
    }

    private String extractFilenameFromUrl(String url) {
        if (url == null) return null;
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            if (path == null) return null;
            int idx = path.lastIndexOf('/');
            if (idx >= 0 && idx < path.length() - 1) {
                return path.substring(idx + 1);
            }
            return null;
        } catch (URISyntaxException e) {
            int idx = url.lastIndexOf('/');
            if (idx >= 0 && idx < url.length() - 1) {
                return url.substring(idx + 1);
            }
            return null;
        }
    }
}