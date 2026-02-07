package com.cooking.dto;


import lombok.Data;

/**
 * 非流式对话响应DTO
 */
@Data
public class ChatResponse {
    /**
     * 响应码：200成功，400参数错误，500系统异常
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 核心回答内容
     */
    private String content;

    /**
     * 生成耗时（毫秒）
     */
    private Long costTime;

    // 快速构建成功响应
    public static ChatResponse success(String content, Long costTime) {
        ChatResponse response = new ChatResponse();
        response.setCode(200);
        response.setMsg("success");
        response.setContent(content);
        response.setCostTime(costTime);
        return response;
    }

    // 快速构建失败响应
    public static ChatResponse fail(Integer code, String msg) {
        ChatResponse response = new ChatResponse();
        response.setCode(code);
        response.setMsg(msg);
        return response;
    }
}
