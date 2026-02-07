package com.cooking.config;



import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 *
 * @author: xyw
 * @date: 2026/2/6 17:37
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LlmConfig {
    /**
     * 大模型接口地址
     */
    private String baseUrl;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * 超时配置
     */
    private Timeout timeout;

    /**
     * 是否流式响应
     */
    private boolean stream;

    /**
     * 最大生成令牌数
     */
    private Integer maxTokens;

    /**
     * 温度（随机性）
     */
    private Double temperature;

    /**
     * 采样阈值
     */
    private Double topP;

    /**
     * 重复惩罚
     */
    private Double repeatPenalty;

    /**
     * 停止词
     */
    private List<String> stop;

    /**
     * 超时配置内部类
     */
    @Data
    public static class Timeout {
        /**
         * 连接超时（毫秒）
         */
        private Integer connect;

        /**
         * 读取超时（毫秒）
         */
        private Integer read;
    }
}