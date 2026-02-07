package com.cooking.dto;



import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 *
 * @author: xyw
 * @date: 2026/2/6 17:39
 */
@Data
public class ChatRequest {
    /**
     * 用户ID（用于多轮对话上下文）
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 用户提问内容
     */
    @NotBlank(message = "提问内容不能为空")
    @Size(max = 500, message = "提问内容不能超过500字")
    private String question;

    /**
     * 会话ID（用于区分不同会话的上下文）
     */
    private Long conversationId;

    /**
     * 是否使用流式响应（优先级高于全局配置）
     */
    private Boolean stream;
}
