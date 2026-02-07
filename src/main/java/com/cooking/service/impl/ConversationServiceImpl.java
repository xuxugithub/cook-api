package com.cooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cooking.entity.Conversation;
import com.cooking.entity.Message;
import com.cooking.mapper.ConversationMapper;
import com.cooking.mapper.MessageMapper;
import com.cooking.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会话服务实现
 */
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {
    
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;
    
    @Override
    @Transactional
    public Conversation createConversation(Long userId, String title) {
        // 检查用户会话数量，最多10个
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getUserId, userId)
               .eq(Conversation::getIsDeleted, 0);
        Long count = conversationMapper.selectCount(wrapper);
        
        if (count >= 10) {
            throw new RuntimeException("会话数量已达上限（10个）");
        }
        
        Conversation conversation = new Conversation();
        conversation.setUserId(userId);
        conversation.setTitle(title);
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());
        conversation.setIsDeleted(0);
        
        conversationMapper.insert(conversation);
        return conversation;
    }
    
    @Override
    public List<Conversation> getUserConversations(Long userId) {
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getUserId, userId)
               .eq(Conversation::getIsDeleted, 0)
               .orderByDesc(Conversation::getUpdateTime);
        return conversationMapper.selectList(wrapper);
    }
    
    @Override
    public List<Message> getConversationMessages(Long conversationId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getConversationId, conversationId)
               .orderByAsc(Message::getSendTime);
        return messageMapper.selectList(wrapper);
    }
    
    @Override
    @Transactional
    public Message saveMessage(Long conversationId, String sender, String content, String messageType) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSender(sender);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setSendTime(LocalDateTime.now());
        
        messageMapper.insert(message);
        
        // 更新会话的updateTime
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.updateById(conversation);
        }
        
        return message;
    }
    
    @Override
    @Transactional
    public boolean deleteConversation(Long conversationId) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            conversation.setIsDeleted(1);
            return conversationMapper.updateById(conversation) > 0;
        }
        return false;
    }
    
    @Override
    public boolean updateConversationTitle(Long conversationId, String title) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            conversation.setTitle(title);
            conversation.setUpdateTime(LocalDateTime.now());
            return conversationMapper.updateById(conversation) > 0;
        }
        return false;
    }
}
