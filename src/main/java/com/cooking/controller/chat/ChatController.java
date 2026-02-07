package com.cooking.controller.chat;

import com.cooking.dto.ChatRequest;
import com.cooking.dto.ChatResponse;
import com.cooking.service.LlmService;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 对话控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor

public class ChatController {
    private final LlmService llmService;

    /**
     * 流式对话接口（核心）- 支持GET和POST
     * @param request   对话请求
     * @param bindingResult 参数校验结果
     * @param httpRequest HTTP请求对象
     * @return  SseEmitter
     */
    @PostMapping("/stream")
    public SseEmitter streamChatPost(@Valid @RequestBody ChatRequest request, 
                                  BindingResult bindingResult,
                                  HttpServletRequest httpRequest) {
        return handleStreamChat(request, bindingResult, httpRequest);
    }

    /**
     * 流式对话接口（GET方式，用于EventSource）
     */
    @GetMapping("/stream")
    public SseEmitter streamChatGet(@RequestParam(required = false) String userId,
                                     @RequestParam String question,
                                     @RequestParam(required = false) Long conversationId,
                                     @RequestParam(required = false, defaultValue = "true") Boolean stream,
                                     HttpServletRequest httpRequest) {
        // 从请求属性中获取userId（由拦截器解析token后设置）
        Long userIdFromToken = (Long) httpRequest.getAttribute("userId");
        
        ChatRequest request = new ChatRequest();
        request.setUserId(userIdFromToken != null ? String.valueOf(userIdFromToken) : userId);
        request.setQuestion(question);
        request.setConversationId(conversationId);
        request.setStream(stream);
        
        return handleStreamChat(request, null, httpRequest);
    }

    /**
     * 统一处理流式对话
     */
    private SseEmitter handleStreamChat(ChatRequest request, BindingResult bindingResult, HttpServletRequest httpRequest) {
        // 从请求属性中获取userId（由拦截器解析token后设置）
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId != null) {
            request.setUserId(String.valueOf(userId));
        }
        
        // 1. 参数校验
        if (bindingResult != null && bindingResult.hasErrors()) {
            String errorMsg = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            SseEmitter emitter = new SseEmitter(60_000L);
            try {
                emitter.send(SseEmitter.event().name("error").data(errorMsg));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }

        // 手动校验（GET请求）
        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            SseEmitter emitter = new SseEmitter(60_000L);
            try {
                emitter.send(SseEmitter.event().name("error").data("提问内容不能为空"));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }

        // 2. 创建SSE发射器（超时60秒）
        SseEmitter emitter = new SseEmitter(60_000L);
        
        // 使用原子布尔值来跟踪emitter是否已完成，避免重复操作
        AtomicBoolean isCompleted = new AtomicBoolean(false);

        // 3. 异步调用大模型
        llmService.callLlmStream(request.getUserId(), request.getConversationId(), request.getQuestion(), emitter, request.getStream());

        // 4. 超时/异常兜底 - 添加同步控制
        emitter.onTimeout(() -> {
            if (isCompleted.compareAndSet(false, true)) {
                try {
                    emitter.send(SseEmitter.event().name("error").data("请求超时，请重试"));
                    emitter.complete();
                } catch (Exception e) {
                    log.error("SSE超时处理失败", e);
                    // 如果send失败，直接标记为完成
                    try {
                        emitter.complete();
                    } catch (Exception ex) {
                        log.error("SSE超时完成失败", ex);
                    }
                }
            }
        });

        emitter.onError(e -> {
            if (isCompleted.compareAndSet(false, true)) {
                try {
                    emitter.send(SseEmitter.event().name("error").data("连接异常：" + e.getMessage()));
                    emitter.complete();
                } catch (Exception ex) {
                    log.error("SSE异常处理失败", ex);
                    // 如果send失败，直接标记为完成
                    try {
                        emitter.complete();
                    } catch (Exception exc) {
                        log.error("SSE异常完成失败", exc);
                    }
                }
            }
        });

        // 监听完成事件，更新状态
        emitter.onCompletion(() -> {
            isCompleted.set(true);
        });

        return emitter;
    }



    /**
     * 非流式对话接口
     * @param request   对话请求
     * @param bindingResult 参数校验结果
     * @param httpRequest HTTP请求对象
     * @return  ChatResponse
     */
    @PostMapping("/sync")
    public ChatResponse syncChat(@Valid @RequestBody ChatRequest request, 
                                  BindingResult bindingResult,
                                  HttpServletRequest httpRequest) {
        long startTime = System.currentTimeMillis();
        
        // 从请求属性中获取userId（由拦截器解析token后设置）
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId != null) {
            request.setUserId(String.valueOf(userId));
        }

        // 1. 参数校验
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError().getDefaultMessage();
            return ChatResponse.fail(HttpStatus.BAD_REQUEST.value(), errorMsg);
        }

        try {
            // 2. 调用大模型
            String content = llmService.callLlmSync(request.getUserId(),request.getConversationId(), request.getQuestion());
            long costTime = System.currentTimeMillis() - startTime;
            return ChatResponse.success(content, costTime);
        } catch (Exception e) {
            log.error("非流式对话异常", e);
            return ChatResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

}