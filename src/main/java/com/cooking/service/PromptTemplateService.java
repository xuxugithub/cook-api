package com.cooking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Prompt模板服务
 */
@Service
@RequiredArgsConstructor
public class PromptTemplateService {
    private final ChatCacheService chatCacheService;

    /**
     * 构建基础Prompt（单轮对话）
     * @param question  用户提问
     * @return  带模板的Prompt
     */
    public String buildSinglePrompt(String question) {
        return String.format("### User: %s ### Assistant: ", question);
    }

    /**
     * 构建带上下文的Prompt（多轮对话）
     * @param userId    用户ID
     * @param question  本次提问
     * @return  带上下文的Prompt
     */
    public String buildContextPrompt(String userId,Long conversationId,String question) {
        String history = chatCacheService.getChatContext(userId,conversationId);
        if (history == null || history.isEmpty()) {
            return buildSinglePrompt(question);
        }
        return history + buildSinglePrompt(question);
    }
}
