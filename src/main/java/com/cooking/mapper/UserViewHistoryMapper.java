package com.cooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cooking.entity.UserViewHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户浏览历史Mapper
 */
@Mapper
public interface UserViewHistoryMapper extends BaseMapper<UserViewHistory> {
    
    /**
     * 获取用户浏览最多的菜品ID列表（按最后浏览时间排序）
     */
    @Select("SELECT dish_id FROM user_view_history WHERE user_id = #{userId} ORDER BY last_view_time DESC LIMIT #{limit}")
    List<Long> getUserMostViewedDishIds(@Param("userId") Long userId, @Param("limit") Integer limit);
}