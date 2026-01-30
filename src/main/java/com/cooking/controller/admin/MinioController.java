package com.cooking.controller.admin;

import com.cooking.common.Result;
import com.cooking.util.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * MinIO文件管理接口
 */
@RestController
@RequestMapping("/api/admin/file")
@RequiredArgsConstructor
@Slf4j
public class MinioController {

    private final MinioUtil minioUtil;

    @Value("${minio.bucketName}")
    private String defaultBucketName;

    /**
     * 文件上传接口
     * @param file 上传的文件
     * @return 上传结果（包含文件访问路径）
     */
    /**
     * 文件上传接口
     * @param file 上传的文件
     * @return 上传结果（包含文件名称）
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 校验文件是否为空
            if (file.isEmpty()) {
                return Result.error("上传失败：文件为空");
            }
            
            // 校验文件类型（只支持常用图片类型）
            String contentType = file.getContentType();
            if (!isValidImageType(contentType)) {
                return Result.error("上传失败：只支持 JPG、PNG、GIF、WEBP 格式的图片");
            }
            
            // 校验文件大小（最大10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.error("上传失败：文件大小不能超过10MB");
            }
            
            log.info("开始上传文件：{}, 大小：{} bytes, 类型：{}", 
                    file.getOriginalFilename(), file.getSize(), contentType);
            
            // 调用工具类上传，返回文件名
            String fileName = minioUtil.uploadFile(file);
            
            Map<String, String> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("originalName", file.getOriginalFilename());
            result.put("contentType", contentType);
            result.put("size", String.valueOf(file.getSize()));
            
            log.info("文件上传成功：{} -> {}", file.getOriginalFilename(), fileName);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 验证是否为支持的图片类型
     * @param contentType 文件类型
     * @return 是否支持
     */
    private boolean isValidImageType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return "image/jpeg".equals(contentType) ||
               "image/jpg".equals(contentType) ||
               "image/png".equals(contentType) ||
               "image/gif".equals(contentType) ||
               "image/webp".equals(contentType);
    }

    /**
     * 文件预览接口（通过文件名直接访问）
     * @param fileName MinIO中存储的文件名
     * @param response 响应对象
     */
    @GetMapping("/preview/{fileName:.+}")
    public void previewFile(@PathVariable String fileName, HttpServletResponse response) {
        try {
            log.info("预览文件：{}", fileName);
            
            // 使用try-with-resources确保流正确关闭
            try (InputStream inputStream = minioUtil.getFileInputStream(fileName);
                 OutputStream outputStream = response.getOutputStream()) {
                
                // 根据文件扩展名设置Content-Type
                String contentType = getContentTypeByFileName(fileName);
                response.setContentType(contentType);
                
                // 设置缓存头
                response.setHeader("Cache-Control", "max-age=3600");
                response.setHeader("Pragma", "cache");
                
                // 将文件流写入响应
                byte[] buffer = new byte[1024 * 8];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();
                
                log.info("文件预览成功：{}", fileName);
            }

        } catch (Exception e) {
            log.error("文件预览失败：{}", fileName, e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            try {
                response.getWriter().write("文件不存在或已被删除");
            } catch (Exception ex) {
                log.error("写入错误信息失败", ex);
            }
        }
    }
    
    /**
     * 根据文件名获取Content-Type
     * @param fileName 文件名
     * @return Content-Type
     */
    private String getContentTypeByFileName(String fileName) {
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
            default:
                return "application/octet-stream";
        }
    }
}