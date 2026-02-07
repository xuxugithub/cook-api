package com.cooking.service;

import com.cooking.entity.Conversation;
import com.cooking.entity.Message;

import java.util.List;

/**
 * 会话服务接口
 */
public interface ConversationService {
    
    /**
     * 创建新会话
     */
    Conversation createConversation(Long userId, String title);
    
    /**
     * 获取用户的会话列表
     */
    List<Conversation> getUserConversations(Long userId);
    
    /**
     * 获取会话详情（包含消息）
     */
    List<Message> getConversationMessages(Long conversationId);
    
    /**
     * 保存消息
     */
    Message saveMessage(Long conversationId, String sender, String content, String messageType);
    
    /**
     * 删除会话
     */
    boolean deleteConversation(Long conversationId);
    
    /**
     * 更新会话标题
     */
    boolean updateConversationTitle(Long conversationId, String title);
}
