package com.cooking.config;



import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * MinIO配置类，读取yml中的配置并创建MinioClient实例
 */
@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioConfig {

    // MinIO服务地址（如http://192.168.1.100:9000）
    private String endpoint;
    // 访问密钥（账号）
    private String accessKey;
    // 密钥（密码）
    private String secretKey;
    // 默认桶名
    private String bucketName;

    /**
     * 创建MinioClient客户端实例，交给Spring容器管理
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
    }
}
