package com.cooking.util;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * MinIO工具类，封装文件上传方法
 */
@Component
@RequiredArgsConstructor // 自动注入MinioClient
@Slf4j // 日志
public class MinioUtil {

    private final MinioClient minioClient;

    // 从yml中读取配置
    @Value("${minio.bucketName}")
    private String defaultBucketName;
    
    @Value("${minio.endpoint}")
    private String minioEndpoint;

    /**
     * 上传文件到MinIO（使用默认桶）
     * @param file 前端上传的文件（MultipartFile）
     * @return 文件在MinIO中的访问路径
     */
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, defaultBucketName);
    }

    /**
     * 上传文件到MinIO（自定义桶）
     * @param file 前端上传的文件
     * @param bucketName 桶名
     * @return 文件名称
     */
    public String uploadFile(MultipartFile file, String bucketName) {
        try {
            // 1. 检查桶是否存在，不存在则创建
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("创建MinIO桶：{}", bucketName);
            }

            // 2. 生成唯一文件名（避免重复）：UUID + 原文件名后缀
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

            // 3. 使用try-with-resources确保输入流正确关闭
            try (InputStream inputStream = file.getInputStream()) {
                // 4. 上传文件到MinIO
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucketName) // 桶名
                        .object(fileName)   // 存储的文件名
                        .stream(inputStream, file.getSize(), -1) // 文件流、大小
                        .contentType(file.getContentType()) // 文件类型
                        .build()
                );
            }

            log.info("文件上传成功，文件名：{}", fileName);
            return fileName; // 直接返回文件名

        } catch (Exception e) {
            log.error("MinIO文件上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件的完整访问URL
     * @param fileName 文件名
     * @param bucketName 桶名
     * @return 完整的文件访问URL
     */
    public String getFileUrl(String fileName, String bucketName) {
        try {
            // 方案1：尝试使用预签名URL（推荐，安全且无跨域问题）
            try {
                String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileName)
                        .expiry(60 * 60 * 24 * 7) // 7天有效期
                        .build()
                );
                log.info("生成预签名URL：{}", presignedUrl);
                return presignedUrl;
            } catch (Exception e) {
                log.warn("生成预签名URL失败，使用直接访问URL", e);
            }
            
            // 方案2：直接访问URL（需要MinIO配置为公共访问）
            String directUrl = minioEndpoint + "/" + bucketName + "/" + fileName;
            log.info("生成直接访问URL：{}", directUrl);
            return directUrl;
            
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            throw new RuntimeException("获取文件URL失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件的完整访问URL（使用默认桶）
     * @param fileName 文件名
     * @return 完整的文件访问URL
     */
    public String getFileUrl(String fileName) {
        return getFileUrl(fileName, defaultBucketName);
    }

    /**
     * 获取预签名URL（用于临时访问）
     * @param fileName 文件名
     * @param bucketName 桶名
     * @param expiry 过期时间（秒）
     * @return 预签名URL
     */
    public String getPresignedUrl(String fileName, String bucketName, int expiry) {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileName)
                    .expiry(expiry)
                    .build()
            );
        } catch (Exception e) {
            log.error("获取预签名URL失败", e);
            // 如果预签名URL生成失败，返回直接访问URL
            return getFileUrl(fileName, bucketName);
        }
    }


    /**
     * 获取MinIO中文件的输入流（供下载使用）
     * @param fileName 存储在MinIO中的文件名（即上传时生成的UUID文件名）
     * @param bucketName 桶名
     * @return 文件输入流
     */
    public InputStream getFileInputStream(String fileName, String bucketName) {
        try {
            // 1. 校验桶是否存在
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                log.error("MinIO桶不存在：{}", bucketName);
                throw new RuntimeException("存储桶不存在：" + bucketName);
            }

            // 2. 获取文件输入流（核心：GetObjectArgs构建下载参数）
            InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
            log.info("获取MinIO文件输入流成功：{}", fileName);
            return inputStream;

        } catch (Exception e) {
            log.error("获取MinIO文件输入流失败", e);
            throw new RuntimeException("获取文件失败：" + e.getMessage());
        }
    }

    /**
     * 重载方法：使用默认桶获取文件输入流
     * @param fileName 存储在MinIO中的文件名
     * @return 文件输入流
     */
    public InputStream getFileInputStream(String fileName) {
        return getFileInputStream(fileName, defaultBucketName);
    }




}
