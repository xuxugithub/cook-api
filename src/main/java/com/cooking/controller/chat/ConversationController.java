package com.cooking.controller.chat;

import com.cooking.common.Result;
import com.cooking.entity.Conversation;
import com.cooking.entity.Message;
import com.cooking.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 会话管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/conversation")
@RequiredArgsConstructor
public class ConversationController {
    
    private final ConversationService conversationService;
    
    /**
     * 创建新会话
     */
    @PostMapping("/create")
    public Result<Conversation> createConversation(@RequestBody Map<String, String> params,
                                                     HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        String title = params.getOrDefault("title", "新对话");
        
        try {
            Conversation conversation = conversationService.createConversation(userId, title);
            return Result.success(conversation);
        } catch (Exception e) {
            log.error("创建会话失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的会话列表
     */
    @GetMapping("/list")
    public Result<List<Conversation>> getConversations(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        List<Conversation> conversations = conversationService.getUserConversations(userId);
        return Result.success(conversations);
    }
    
    /**
     * 获取会话的消息列表
     */
    @GetMapping("/{conversationId}/messages")
    public Result<List<Message>> getMessages(@PathVariable Long conversationId) {
        List<Message> messages = conversationService.getConversationMessages(conversationId);
        return Result.success(messages);
    }
    
    /**
     * 保存消息
     */
    @PostMapping("/message")
    public Result<Message> saveMessage(@RequestBody Map<String, Object> params) {
        Long conversationId = Long.valueOf(params.get("conversationId").toString());
        String sender = (String) params.get("sender");
        String content = (String) params.get("content");
        String messageType = (String) params.getOrDefault("messageType", "TEXT");
        
        try {
            Message message = conversationService.saveMessage(conversationId, sender, content, messageType);
            return Result.success(message);
        } catch (Exception e) {
            log.error("保存消息失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除会话
     */
    @DeleteMapping("/{conversationId}")
    public Result<Boolean> deleteConversation(@PathVariable Long conversationId) {
        boolean success = conversationService.deleteConversation(conversationId);
        return success ? Result.success(true) : Result.error("删除失败");
    }
    
    /**
     * 更新会话标题
     */
    @PutMapping("/{conversationId}/title")
    public Result<Boolean> updateTitle(@PathVariable Long conversationId,
                                        @RequestBody Map<String, String> params) {
        String title = params.get("title");
        boolean success = conversationService.updateConversationTitle(conversationId, title);
        return success ? Result.success(true) : Result.error("更新失败");
    }
}
