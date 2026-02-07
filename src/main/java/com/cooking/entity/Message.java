package com.cooking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息实体
 */
@Data
@TableName("message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long conversationId;
    
    private String sender; // USER/AI/SYSTEM
    
    private String content;
    
    private LocalDateTime sendTime;
    
    private String messageType; // TEXT/IMAGE/FILE
}
