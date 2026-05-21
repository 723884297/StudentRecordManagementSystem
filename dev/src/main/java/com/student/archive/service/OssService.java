package com.student.archive.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.student.archive.config.OssConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OssService {

    private final OSS ossClient;
    private final OssConfig ossConfig;

    /**
     * 上传档案文件到OSS
     */
    public String uploadArchiveFile(MultipartFile file, String originalFileName) {
        return upload(file, ossConfig.getPrefixArchive(), originalFileName);
    }

    /**
     * 上传图片到OSS
     */
    public String uploadImage(MultipartFile file, String originalFileName) {
        return upload(file, ossConfig.getPrefixImage(), originalFileName);
    }

    /**
     * 通过InputStream上传到OSS（用于审核通过后从临时文件上传）
     */
    public String uploadArchiveFile(InputStream inputStream, long contentLength, String originalFileName) {
        String ext = getExtension(originalFileName);
        String objectKey = ossConfig.getPrefixArchive() + UUID.randomUUID().toString().replace("-", "") + ext;
        try {
            ossClient.putObject(ossConfig.getBucket(), objectKey, inputStream);
            String url = ossConfig.getBaseUrl() + "/" + objectKey;
            log.info("OSS上传成功: {}", url);
            return url;
        } catch (Exception e) {
            log.error("OSS上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传OSS失败: " + e.getMessage());
        }
    }

    /**
     * 从OSS下载文件对象（用于服务端流式预览/下载）
     */
    public OSSObject getObject(String fileUrl) {
        String objectKey = fileUrl.replace(ossConfig.getBaseUrl() + "/", "");
        return ossClient.getObject(ossConfig.getBucket(), objectKey);
    }

    /**
     * 从OSS删除文件
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;
        try {
            String objectKey = fileUrl.replace(ossConfig.getBaseUrl() + "/", "");
            ossClient.deleteObject(ossConfig.getBucket(), objectKey);
            log.info("OSS删除成功: {}", objectKey);
        } catch (Exception e) {
            log.error("OSS删除失败: {}", e.getMessage(), e);
        }
    }

    private String upload(MultipartFile file, String prefix, String originalFileName) {
        String ext = getExtension(originalFileName);
        String objectKey = prefix + UUID.randomUUID().toString().replace("-", "") + ext;
        try (InputStream is = file.getInputStream()) {
            ossClient.putObject(ossConfig.getBucket(), objectKey, is);
            String url = ossConfig.getBaseUrl() + "/" + objectKey;
            log.info("OSS上传成功: {}", url);
            return url;
        } catch (IOException e) {
            log.error("OSS上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传OSS失败: " + e.getMessage());
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null) return "";
        int idx = fileName.lastIndexOf('.');
        return idx >= 0 ? fileName.substring(idx) : "";
    }
}
