package com.cooking.service;



import com.alibaba.fastjson2.JSONObject;
import com.cooking.config.LlmConfig;
import com.cooking.util.JsonUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 大模型服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LlmService {
    private final LlmConfig llmConfig;
    private final PromptTemplateService promptTemplateService;
    private final ChatCacheService chatCacheService;

    // 初始化OkHttp客户端（全局单例）
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .connectTimeout(10000, TimeUnit.MILLISECONDS)
        .readTimeout(60000, TimeUnit.MILLISECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
        .build();

    /**
     * 流式调用大模型（异步）
     */
    @Async("chatExecutor")
    @CircuitBreaker(name = "llmService", fallbackMethod = "streamFallback")
    @Retry(name = "llmService")
    public void callLlmStream(String userId, Long conversationId, String question, SseEmitter emitter, Boolean useStream) {
        long startTime = System.currentTimeMillis();
        AtomicBoolean isCompleted = new AtomicBoolean(false);
        
        try {
            // 1. 构建Prompt
            String prompt = promptTemplateService.buildContextPrompt(userId,conversationId, question);
            log.info("构建Prompt完成，用户ID：{}，提问：{}", userId, question);

            // 2. 构建请求参数
            boolean stream = useStream != null ? useStream : llmConfig.isStream();
            String requestJson = buildRequestJson(prompt, stream);

            // 3. 构建OkHttp请求
            Request request = new Request.Builder()
                .url(llmConfig.getBaseUrl())
                .post(RequestBody.create(requestJson, MediaType.parse("application/json; charset=utf-8")))
                .addHeader("api-key", llmConfig.getApiKey())
                .build();

            // 4. 执行流式请求（通用逐行读取，兼容所有版本）
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    handleError(emitter, isCompleted, "大模型接口调用失败，状态码：" + response.code());
                    return;
                }
                if (response.body() == null) {
                    handleError(emitter, isCompleted, "大模型响应体为空");
                    return;
                }

                // 核心：BufferedSource逐行读取
                BufferedSource source = response.body().source();
                StringBuilder fullContent = new StringBuilder();
                boolean isStop = false;

                while (!source.exhausted() && !isStop) {
                    String line = source.readUtf8Line();
                    if (line == null || line.isEmpty()) {
                        continue;
                    }

                    try {
                        int dataSeparatorIndex = line.indexOf("data:");
                        if (dataSeparatorIndex == -1) {
                            throw new IllegalArgumentException("原始字符串中未找到\"data:\"分隔符，字符串：" + line);
                        }

                        // 3. 截取data:后的内容，去除首尾空格（处理data:后有空格的情况）
                        String jsonPart = line.substring(dataSeparatorIndex + "data:".length()).trim();
                        JSONObject json = JsonUtils.parseObject(jsonPart);
                        String content = json.getString("content");
                        boolean stop = json.getBooleanValue("stop");

                        String validContent = "";
                        if (content != null && !content.isEmpty()) {
                            // 找到### User: 的位置，截取前面的有效内容
                            int stopWordIndex = content.indexOf("### User: ");
                            if (stopWordIndex != -1) {
                                // 截断并去除首尾空格
                                validContent = content.substring(0, stopWordIndex).trim();
                                // 一旦发现停止词，立即标记停止（不再接收后续片段）
                                isStop =true;
                            } else {
                                // 无停止词，保留原内容
                                validContent = content.trim();
                            }
                        }

                        // ========== 只推送非空的有效内容 ==========
                        if (!validContent.isEmpty() && !isCompleted.get()) {
                            // 拼接完整内容（仅保留有效部分）
                            fullContent.append(validContent);
                            // 向前端推送有效内容
                            emitter.send(SseEmitter.event().name("message").data(validContent));
                        }

                        if (stop || isCompleted.get()) {
                            isStop = true;
                            long costTime = System.currentTimeMillis() - startTime;
                            log.info("流式调用完成，用户ID：{}，会话ID：{}，耗时：{}ms", userId, conversationId, costTime);
                            // 缓存+上下文
                            chatCacheService.cacheAnswer(question, fullContent.toString());
                            chatCacheService.saveChatContext(userId, conversationId, question, fullContent.toString());
                            
                            // 完成推送前检查状态
                            if (isCompleted.compareAndSet(false, true)) {
                                try {
                                    emitter.send(SseEmitter.event().name("complete").data("回答结束，耗时" + costTime + "ms"));
                                    emitter.complete();
                                } catch (Exception e) {
                                    log.error("完成SSE推送失败", e);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("解析流式响应失败：{}", line, e);
                        isStop = true;
                        handleError(emitter, isCompleted, "解析响应失败：" + e.getMessage());
                        return;
                    }
                }

                // 正常结束处理
                if (!isStop && isCompleted.compareAndSet(false, true)) {
                    log.info("流式调用完成，用户ID：{}", userId);
                    try {
                        emitter.send(SseEmitter.event().name("complete").data("回答已全部返回"));
                        emitter.complete();
                    } catch (Exception e) {
                        log.error("完成SSE推送失败", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("流式调用大模型异常，用户ID：{}", userId, e);
            handleError(emitter, isCompleted, "流式调用失败：" + e.getMessage());
        }
    }

    /**
     * 统一错误处理方法
     */
    private void handleError(SseEmitter emitter, AtomicBoolean isCompleted, String errorMessage) {
        if (isCompleted.compareAndSet(false, true)) {
            try {
                emitter.send(SseEmitter.event().name("error").data(errorMessage));
                emitter.complete();
            } catch (Exception e) {
                log.error("推送异常信息失败: {}", errorMessage, e);
                try {
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    log.error("标记SSE完成失败", ex);
                }
            }
        }
    }

    /**
     * 非流式调用大模型
     * @param userId    用户ID
     * @param conversationId  会话ID
     * @param question  提问内容
     * @return  完整回答内容
     */
    @CircuitBreaker(name = "llmService", fallbackMethod = "syncFallback")
    @Retry(name = "llmService")
    public String callLlmSync(String userId,Long conversationId, String question) {
        long startTime = System.currentTimeMillis();
        try {
            // 1. 先查缓存
            String cachedAnswer = SpringContextUtil.getBean(ChatCacheService.class).getCachedAnswer(question);
            if (cachedAnswer != null) {
                log.info("缓存命中，用户ID：{}，提问：{}", userId, question);
                return cachedAnswer;
            }

            // 2. 构建Prompt
            String prompt = promptTemplateService.buildContextPrompt(userId,conversationId, question);

            // 3. 构建请求参数（非流式）
            String requestJson = buildRequestJson(prompt, false);

            // 4. 执行同步请求
            Request request = new Request.Builder()
                .url(llmConfig.getBaseUrl())
                .post(RequestBody.create(requestJson, MediaType.parse("application/json; charset=utf-8")))
                .addHeader("api-key", llmConfig.getApiKey())
                .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("大模型接口调用失败，状态码：" + response.code());
                }

                // 解析完整响应
                String responseBody = response.body().string();
                JSONObject json = JsonUtils.parseObject(responseBody);
                String content = json.getString("content");

                // 缓存回答+保存上下文
                ChatCacheService cacheService = SpringContextUtil.getBean(ChatCacheService.class);
                cacheService.cacheAnswer(question, content);
                cacheService.saveChatContext(userId,conversationId, question, content);

                long costTime = System.currentTimeMillis() - startTime;
                log.info("同步调用完成，用户ID：{}，耗时：{}ms，回答长度：{}", userId, costTime, content.length());
                return content;
            }
        } catch (Exception e) {
            log.error("同步调用大模型异常，用户ID：{}，提问：{}", userId, question, e);
            throw new RuntimeException("同步调用失败：" + e.getMessage(), e);
        }
    }

    /**
     * 构建大模型请求JSON
     */
    private String buildRequestJson(String prompt, boolean stream) {
        HashMap<String,Object> params = new HashMap<>();
        params.put("prompt", prompt);
        params.put("max_tokens", llmConfig.getMaxTokens());
        params.put("temperature", llmConfig.getTemperature());
        params.put("top_p", llmConfig.getTopP());
        params.put("repeat_penalty", llmConfig.getRepeatPenalty());
        params.put("stream", stream);
        params.put("stop", llmConfig.getStop());
        return JsonUtils.toJsonString(params);
    }

    /**
     * 流式调用兜底方法
     */
    public void streamFallback(String userId, Long conversationId, String question, SseEmitter emitter, Boolean useStream, Exception e) {
        log.error("流式调用触发熔断兜底，用户ID：{}，会话ID：{}，异常：{}", userId, conversationId, e.getMessage());
        try {
            emitter.send(SseEmitter.event().name("error").data("当前AI服务繁忙，请稍后再试～"));
            emitter.complete();
        } catch (Exception ex) {
            emitter.completeWithError(ex);
        }
    }

    /**
     * 同步调用兜底方法
     */
    public String syncFallback(String userId, String question, Exception e) {
        log.error("同步调用触发熔断兜底，用户ID：{}，异常：{}", userId, e.getMessage());
        return "非常抱歉，当前AI对话服务暂时不可用，请稍后再试～";
    }

    /**
     * Spring上下文工具（解决异步中获取Bean）
     */
    public static class SpringContextUtil implements org.springframework.context.ApplicationContextAware {
        private static org.springframework.context.ApplicationContext applicationContext;

        @Override
        public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) {
            SpringContextUtil.applicationContext = applicationContext;
        }

        public static <T> T getBean(Class<T> clazz) {
            return applicationContext.getBean(clazz);
        }
    }
}