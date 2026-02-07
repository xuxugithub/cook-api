package com.cooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cooking.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会话Mapper
 */
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {
}
