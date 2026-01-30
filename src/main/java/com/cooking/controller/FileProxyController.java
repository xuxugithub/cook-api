package com.cooking.controller;

import com.cooking.util.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 文件代理控制器 - 用于代理访问MinIO文件，解决跨域问题
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileProxyController {

    private final MinioUtil minioUtil;

    /**
     * 代理访问MinIO文件
     * @param bucketName 桶名
     * @param fileName 文件名
     * @return 文件内容
     */
    @GetMapping("/{bucketName}/{fileName:.+}")
    public ResponseEntity<byte[]> getFile(@PathVariable String bucketName, @PathVariable String fileName) {
        try {
            log.info("代理访问文件：{}/{}", bucketName, fileName);
            
            // 使用try-with-resources确保流正确关闭
            try (InputStream inputStream = minioUtil.getFileInputStream(fileName, bucketName);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                
                // 将输入流转换为字节数组
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                byte[] fileBytes = outputStream.toByteArray();
                
                // 根据文件扩展名设置Content-Type
                String contentType = getContentType(fileName);
                
                // 设置响应头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.setCacheControl("max-age=3600"); // 缓存1小时
                
                log.info("文件代理访问成功：{}/{}, 大小：{} bytes", bucketName, fileName, fileBytes.length);
                
                return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
            }
            
        } catch (Exception e) {
            log.error("文件代理访问失败：{}/{}", bucketName, fileName, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * 根据文件扩展名获取Content-Type
     * @param fileName 文件名
     * @return Content-Type
     */
    private String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            case "pdf":
                return "application/pdf";
            case "txt":
                return "text/plain";
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
            default:
                return "application/octet-stream";
        }
    }
}