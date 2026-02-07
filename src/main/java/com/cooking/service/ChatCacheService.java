package com.cooking.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 对话缓存服务（热点问题缓存）
 * @author Administrator
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatCacheService {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 缓存对话回答
     * @param question  用户提问
     * @param answer    模型回答
     */
    public void cacheAnswer(String question, String answer) {
        try {
            // 问题MD5哈希作为key，避免特殊字符/过长
            String key = "chat:answer:" + DigestUtils.md5Hex(question);
            // 缓存1小时
            stringRedisTemplate.opsForValue().set(key, answer, 1, TimeUnit.HOURS);
            log.info("缓存问答成功，key：{}", key);
        } catch (Exception e) {
            log.error("缓存问答失败", e);
            // 缓存失败不影响主流程，仅打印日志
        }
    }

    /**
     * 获取缓存的回答
     * @param question  用户提问
     * @return  缓存的回答（null表示未命中）
     */
    public String getCachedAnswer(String question) {
        try {
            String key = "chat:answer:" + DigestUtils.md5Hex(question);
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("获取缓存问答失败", e);
            return null;
        }
    }

    /**
     * 保存多轮对话上下文
     * @param userId    用户ID
     * @param conversationId 会话ID
     * @param question  本次提问
     * @param answer    本次回答
     */
    public void saveChatContext(String userId, Long conversationId, String question, String answer) {
        try {
            String key = "chat:context:" + userId + ":" + conversationId;
            String history = stringRedisTemplate.opsForValue().get(key);
            if (history == null) {
                history = "";
            }
            // 拼接上下文（控制长度，避免令牌超限）
            String newHistory = history + "### User: " + question + " ### Assistant: " + answer + "\n";
            // 30分钟过期
            stringRedisTemplate.opsForValue().set(key, newHistory, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("保存对话上下文失败", e);
        }
    }

    /**
     * 获取多轮对话上下文
     * @param userId    用户ID
     * @param conversationId 会话ID
     * @return  上下文字符串
     */
    public String getChatContext(String userId, Long conversationId) {
        try {
            String key = "chat:context:" + userId + ":" + conversationId;
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("获取对话上下文失败", e);
            return null;
        }
    }
    
    /**
     * 清除会话上下文
     * @param userId    用户ID
     * @param conversationId 会话ID
     */
    public void clearChatContext(String userId, Long conversationId) {
        try {
            String key = "chat:context:" + userId + ":" + conversationId;
            stringRedisTemplate.delete(key);
            log.info("清除会话上下文成功，key：{}", key);
        } catch (Exception e) {
            log.error("清除会话上下文失败", e);
        }
    }
}
